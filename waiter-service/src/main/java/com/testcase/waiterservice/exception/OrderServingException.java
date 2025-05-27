package com.testcase.waiterservice.exception;

/**
 * Исключение, возникающее при ошибке обслуживания заказа.
 * Это исключение выбрасывается, когда происходит сбой в процессе обслуживания
 * заказа, например, несоответствие статуса или отсутствие оплаты.
 */
public class OrderServingException extends RuntimeException {
    /**
     * Конструктор, который инициализирует исключение с заданным сообщением.
     *
     * @param message сообщение, описывающее причину ошибки обслуживания заказа
     */
    public OrderServingException(String message) {
        super(message);
    }
}
