package com.testcase.waiterservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Конфигурация для настройки {@link ObjectMapper}.
 * <p>
 * Создаёт и настраивает {@link ObjectMapper} с использованием модуля {@link JavaTimeModule} для работы с временем,
 * отключает сериализацию времени как timestamp и настраивает поведение при десериализации.
 * </p>
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * Создаёт и настраивает {@link ObjectMapper}.
     * <p>
     * Модуль {@link JavaTimeModule} регистрируется для работы с типами времени,
     * также отключается сериализация дат в виде timestamp и настраивается игнорирование неизвестных свойств
     * при десериализации.
     * </p>
     *
     * @return настроенный экземпляр {@link ObjectMapper}
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
