package com.testcase.waiterservice.exception;

/**
 * Исключение, возникающее при неуспешной оплате заказа.
 * Это исключение выбрасывается в случае, когда оплата не может быть обработана
 * по разным причинам, таким как недостаточность средств или ошибка обработки.
 */
public class UnsuccessfulPaymentException extends RuntimeException {
    /**
     * Конструктор, который инициализирует исключение с заданным сообщением.
     *
     * @param message сообщение, описывающее причину неудачной оплаты
     */
    public UnsuccessfulPaymentException(String message) {
        super(message);
    }
}
