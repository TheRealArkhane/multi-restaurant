package com.testcase.kitchenservice.exception;

/**
 * Исключение, выбрасываемое, если заказ на кухне не найден.
 */
public class KitchenOrderNotFoundException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение ошибки
     */
    public KitchenOrderNotFoundException(String message) {
        super(message);
    }
}
