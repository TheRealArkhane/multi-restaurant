package com.testcase.waiterservice.exception;

/**
 * Исключение, выбрасываемое, если платеж с указанным ID не найден.
 */
public class PaymentNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
