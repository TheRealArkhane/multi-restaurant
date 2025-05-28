package com.testcase.waiterservice.client;

import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderValidationDTO;

public interface ValidationClient {

    /**
     * Отправляет запрос на сервис кухни для валидации заказа.
     * Этот метод используется официантом для проверки, можно ли отправить заказ на кухню,
     * проверяя баланс выбранных в заказе блюд в БД kitchen-service.
     *
     * @param validationDTO объект {@link OrderDTO}, содержащий информацию о заказе
     * @return {@link Boolean} результат валидации (true — заказ валиден, иначе поступит сообщение об ошибке)
     */
    Boolean validateOrder(OrderValidationDTO validationDTO);
}
