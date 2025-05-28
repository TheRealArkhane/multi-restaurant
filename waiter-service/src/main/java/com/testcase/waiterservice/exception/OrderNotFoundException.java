package com.testcase.waiterservice.exception;

/**
 * Исключение, выбрасываемое, если заказ с указанным ID не найден.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
}
