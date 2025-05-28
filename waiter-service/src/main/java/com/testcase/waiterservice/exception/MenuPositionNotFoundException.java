package com.testcase.waiterservice.exception;

/**
 * Исключение, возникающее, когда позиция меню не найдена в базе данных.
 * Это исключение выбрасывается, если попытаться обратиться к несуществующей позиции меню.
 */
public class MenuPositionNotFoundException extends RuntimeException {
    /**
     * Конструктор, который инициализирует исключение с заданным сообщением.
     *
     * @param message сообщение, описывающее причину, по которой позиция меню не была найдена
     */
    public MenuPositionNotFoundException(String message) {
        super(message);
    }
}
