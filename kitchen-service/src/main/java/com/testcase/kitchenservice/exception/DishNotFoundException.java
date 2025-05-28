package com.testcase.kitchenservice.exception;

/**
 * Исключение, выбрасываемое, если блюдо не найдено в БД кухни
 */
public class DishNotFoundException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение ошибки
     */
    public DishNotFoundException(String message) {
        super(message);
    }
}
