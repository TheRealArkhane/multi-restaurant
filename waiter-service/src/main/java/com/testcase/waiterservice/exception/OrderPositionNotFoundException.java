package com.testcase.waiterservice.exception;

/**
 * Исключение, возникающее, когда позиция заказа не найдена в базе данных.
 * Это исключение выбрасывается, если попытаться обратиться к несуществующей позиции заказа.
 */
public class OrderPositionNotFoundException extends RuntimeException {
    /**
     * Конструктор, который инициализирует исключение с заданным сообщением.
     *
     * @param message сообщение, описывающее причину, по которой позиция заказа не была найдена
     */
    public OrderPositionNotFoundException(String message) {
        super(message);
    }
}
