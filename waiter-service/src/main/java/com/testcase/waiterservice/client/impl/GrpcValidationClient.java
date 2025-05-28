package com.testcase.waiterservice.client.impl;

import com.education.commondto.waiterservice.OrderValidationDTO;
import com.education.grpc.GrpcOrderValidationDTOProto;
import com.education.grpc.KitchenValidationServiceGrpc;
import com.education.grpc.ValidationResponse;
import com.education.waiterservice.client.ValidationClient;
import com.education.waiterservice.mapper.OrderValidationToGrpcMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Конфигурация для запуска gRPC сервера.
 * <p>
 * Настроен gRPC сервер, который прослушивает указанный порт и обслуживает запросы на валидацию заказа.
 * Также добавлен хук для корректного завершения работы сервера при завершении приложения.
 * </p>
 */
@Slf4j
@Service
@Profile("grpc")
@ConditionalOnProperty(prefix = "grpc.client", name = "type", havingValue = "grpc")
public class GrpcValidationClient implements ValidationClient {

    private final String host;
    private final int port;
    private final OrderValidationToGrpcMapper mapper;

    /**
     * Конструктор клиента.
     *
     * @param host   хост gRPC-сервера
     * @param port   порт gRPC-сервера
     * @param mapper маппер DTO → gRPC DTO
     */
    public GrpcValidationClient(@Value("${grpc.client.host}") String host,
                                            @Value("${grpc.client.port}") int port,
                                            OrderValidationToGrpcMapper mapper) {
        this.host = host;
        this.port = port;
        this.mapper = mapper;
    }

    /**
     * Метод валидации заказа через gRPC kitchen-service.
     *
     * @param validationDTO DTO с заказом и блюдами
     * @return true — если валидация прошла успешно
     * @throws IllegalArgumentException если блюд недостаточно
     */
    @Override
    public Boolean validateOrder(OrderValidationDTO validationDTO) {
        log.info("Валидация через gRPC:");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            KitchenValidationServiceGrpc.KitchenValidationServiceBlockingStub stub =
                    KitchenValidationServiceGrpc.newBlockingStub(channel);

            GrpcOrderValidationDTOProto grpcRequest = mapper.toGrpc(validationDTO);
            ValidationResponse response = stub.validateOrder(grpcRequest);

            if (!response.getValid()) {
                String message = String.join("; ", response.getErrorsList());
                throw new IllegalArgumentException("Ошибка валидации заказа: " + message);
            }
            return true;
        } finally {
            channel.shutdown();
        }
    }
}
