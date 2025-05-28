package com.testcase.waiterservice.client.impl;

import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.waiterservice.client.ValidationClient;
import feign.Headers;
import feign.RequestLine;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign-клиент для взаимодействия с сервисом кухни.
 * Этот клиент используется для проверки баланса выбранных в заказе блюд в БД kitchen-service.
 * Взаимодействует с внешним сервисом кухни, вызывая его эндпоинты для валидации информации о заказах.
 */
@Profile("!grpc")
public interface FeignValidationClient extends ValidationClient {

    @Override
    @RequestLine("POST /internal/kitchen/orders/validate")
    @Headers("Content-Type: application/json")
    Boolean validateOrder(@RequestBody OrderValidationDTO validationDTO);
}
