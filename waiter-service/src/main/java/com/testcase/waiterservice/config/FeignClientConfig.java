package com.testcase.waiterservice.config;

import com.testcase.waiterservice.client.impl.FeignValidationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация Feign-клиента для взаимодействия с сервисом кухни.
 * Настраивает URL, таймауты, уровень логирования и сериализацию/десериализацию JSON с помощью Jackson.
 * Также настраивает параметры Feign-клиента для работы с сервисом кухни.
 */
@Configuration
@Profile("!grpc")
public class FeignClientConfig {

    private final String kitchenServiceUrl;
    private final Integer connectTimeout;
    private final Integer readTimeout;
    private final Logger.Level loggerLevel;
    private final ObjectMapper objectMapper;

    /**
     * Конструктор конфигурации Feign-клиента.
     * <p>
     * Значения свойств берутся из application.yml,
     * при отсутствии значения подставляются дефолтные.
     * </p>
     *
     * @param kitchenServiceUrl URL кухонного сервиса (по умолчанию http://localhost:8081)
     * @param connectTimeout    время ожидания соединения (в мс, по умолчанию 5000)
     * @param readTimeout       время ожидания ответа (в мс, по умолчанию 5000)
     * @param loggerLevelStr    строковое представление уровня логирования (по умолчанию, FULL)
     * @param objectMapper      {@link ObjectMapper} для работы с JSON
     */
    public FeignClientConfig(
            @Value("${waiter-service.url:http://localhost:8081}") String kitchenServiceUrl,
            @Value("${feign.client.config.default.connectTimeout:5000}") Integer connectTimeout,
            @Value("${feign.client.config.default.readTimeout:5000}") Integer readTimeout,
            @Value("${feign.client.config.default.loggerLevel:FULL}") String loggerLevelStr,
            ObjectMapper objectMapper) {
        this.kitchenServiceUrl = kitchenServiceUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.loggerLevel = Logger.Level.valueOf(loggerLevelStr);
        this.objectMapper = objectMapper;
    }

    /**
     * Определяет bean для {@link FeignValidationClient}.
     * <p>
     * Настраивает Feign с использованием JacksonEncoder, JacksonDecoder, Slf4jLogger и установленными таймаутами.
     * Также конфигурирует уровень логирования и подключает URL для взаимодействия с кухонным сервисом.
     * </p>
     *
     * @return настроенный клиент для взаимодействия с сервисом кухни
     */
    @Bean
    public FeignValidationClient waiterOrderFeignClient() {
        return Feign.builder()
                .encoder(new JacksonEncoder(objectMapper))
                .decoder(new JacksonDecoder(objectMapper))
                .logger(new Slf4jLogger(FeignValidationClient.class))
                .logLevel(loggerLevel)
                .options(new Request.Options(connectTimeout, TimeUnit.MILLISECONDS,
                        readTimeout, TimeUnit.MILLISECONDS, true))
                .target(FeignValidationClient.class, kitchenServiceUrl);
    }
}
