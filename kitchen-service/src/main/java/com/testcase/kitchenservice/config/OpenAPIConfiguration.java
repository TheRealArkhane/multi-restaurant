package com.testcase.kitchenservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI для сервисов.
 * <p>
 * Настраивает информацию о API, сервере, разработчике и прочих метаданных.
 * Данная конфигурация используется для генерации Swagger-автодокументации сервиса кухни.
 * </p>
 */
@Configuration
public class OpenAPIConfiguration {

    private final Integer port;
    private static final String APPLICATION_NAME = "Kitchen Service API";
    private static final String APPLICATION_DESCRIPTION =
            "API сервиса кухни для проекта ресторана в стажировке в Лигу Цифровой Экономики";
    private static final String APPLICATION_VERSION = "1.0.0";
    private static final String DEVELOPER_NAME = "Виктор Федянин";
    private static final String DEVELOPER_EMAIL = "fedyanin.v.v@yandex.ru";

    /**
     * Конструктор конфигурации OpenAPI.
     *
     * @param port порт, на котором запущен сервис, подставляемый через свойство server.port
     */
    public OpenAPIConfiguration(@Value("${server.port}") Integer port) {
        this.port = port;
    }

    /**
     * Определяет bean с настройками OpenAPI.
     * <p>
     * Настраивает сервер, информацию о приложении и контактные данные разработчика.
     * </p>
     *
     * @return объект {@link OpenAPI} с установленными параметрами
     */
    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:" + port);
        server.setDescription(APPLICATION_NAME);

        Contact myContact = new Contact();
        myContact.setName(DEVELOPER_NAME);
        myContact.setEmail(DEVELOPER_EMAIL);

        Info information = new Info()
                .title(APPLICATION_NAME)
                .version(APPLICATION_VERSION)
                .description(APPLICATION_DESCRIPTION)
                .contact(myContact);
        return new OpenAPI()
                .info(information)
                .servers(List.of(server));
    }
}

