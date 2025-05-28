package com.testcase.kitchenservice.exception;

/**
 * Исключение, выбрасываемое при попытке установить недопустимый статус заказа.
 */
public class InvalidOrderStatusException extends IllegalArgumentException {

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение ошибки
     */
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
