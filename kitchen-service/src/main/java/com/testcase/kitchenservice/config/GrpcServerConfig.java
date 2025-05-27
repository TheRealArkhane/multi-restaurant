package com.testcase.kitchenservice.config;

import com.testcase.grpc.KitchenValidationServiceGrpc;
import com.testcase.kitchenservice.grpc.KitchenValidationServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация для запуска gRPC сервера.
 * <p>
 * Настроен gRPC сервер, который прослушивает указанный порт и обслуживает запросы на валидацию заказа.
 * Также добавлен хук для корректного завершения работы сервера при завершении приложения.
 * </p>
 */
@Slf4j
@Configuration
@Profile("grpc")
public class GrpcServerConfig {

    private final Integer grpcPort;

    /**
     * Конструктор для инициализации конфигурации gRPC сервера.
     *
     * @param grpcPort порт, на котором будет слушать gRPC сервер
     */
    public GrpcServerConfig(@Value("${grpc.server.port}") Integer grpcPort) {
        this.grpcPort = grpcPort;
    }

    /**
     * Создаёт и запускает gRPC сервер, если свойство {@code grpc.enabled} установлено в {@code true}.
     * <p>
     * Сервер будет обслуживать сервис {@link KitchenValidationServiceGrpc} на указанном порту.
     * После запуска сервера, добавляется хук для его корректного завершения при завершении приложения.
     * </p>
     *
     * @param kitchenValidationServiceGrpc сервис для обработки запросов на обновление статуса заказа
     * @return экземпляр {@link Server} gRPC сервера
     * @throws Exception если произошла ошибка при запуске сервера
     */
    @ConditionalOnProperty(prefix = "grpc", name = "enabled", havingValue = "true")
    @Bean(destroyMethod = "shutdown")
    public Server grpcServer(
            KitchenValidationServiceImpl kitchenValidationServiceGrpc)
            throws Exception {
        Server server = ServerBuilder.forPort(grpcPort)
                .addService(kitchenValidationServiceGrpc)
                .build()
                .start();

        log.info("gRPC сервер запущен на порту {}", grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Выключение gRPC сервера...");
            server.shutdown();
        }));
        return server;
    }
}
