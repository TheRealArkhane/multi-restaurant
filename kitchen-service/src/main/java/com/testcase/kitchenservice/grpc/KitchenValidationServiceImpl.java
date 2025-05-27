package com.testcase.kitchenservice.grpc;

import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.grpc.GrpcOrderValidationDTOProto;
import com.testcase.grpc.KitchenValidationServiceGrpc;
import com.testcase.grpc.ValidationResponse;
import com.testcase.kitchenservice.mapstruct.mapper.GrpcOrderValidationFromGrpcMapper;
import com.testcase.kitchenservice.service.KitchenOrderService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Реализация gRPC-сервиса для валидации заказа.
 * <p>
 * Данный класс обрабатывает gRPC-запросы на валидацию заказа.
 * В методе {@code validateOrder} происходит преобразование входящего gRPC объекта
 * в внутренний формат DTO, а затем вызывается сервис бизнес-логики для валидации заказа.
 * </p>
 *
 * kitchenValidationService - Сервис бизнес-логики для валидации заказа
 * mapper - Маппер для преобразования между {@link GrpcOrderValidationDTOProto} и {@link OrderValidationDTO}
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("grpc")
public class KitchenValidationServiceImpl extends KitchenValidationServiceGrpc.KitchenValidationServiceImplBase {

    private final KitchenOrderService kitchenOrderService;
    private final GrpcOrderValidationFromGrpcMapper mapper;

    /**
     * Обрабатывает gRPC-вызов на валидацию заказа.
     * <p>
     * Преобразует входящий объект {@link GrpcOrderValidationDTOProto} в {@link OrderValidationDTO},
     * вызывает метод валидации заказа в {@code kitchenValidationService} и возвращает ответ.
     * </p>
     *
     * @param request          Входящий gRPC объект с данными для валидации заказа
     * @param responseObserver Объект для передачи ответа клиенту
     */
    @Override
    public void validateOrder(GrpcOrderValidationDTOProto request,
                              StreamObserver<ValidationResponse> responseObserver) {
        try {
        OrderValidationDTO validationDTO = mapper.fromGrpc(request);
        log.info("Валидация через gRPC:");
        boolean isValid = kitchenOrderService.validateOrder(validationDTO);
        ValidationResponse response = ValidationResponse.newBuilder()
                .setValid(isValid)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
        log.error("Ошибка валидации через gRPC: {}", e.getMessage(), e);
        responseObserver.onError(
                Status.INVALID_ARGUMENT
                        .withDescription(e.getMessage())
                        .withCause(e)
                        .asRuntimeException());
        } catch (Exception e) {
        log.error("Необработанная ошибка валидации через gRPC", e);
        responseObserver.onError(
                Status.UNKNOWN
                        .withDescription("Внутренняя ошибка сервера")
                        .withCause(e)
                        .asRuntimeException()
        );
        }
    }
}
