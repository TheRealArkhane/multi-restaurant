package com.testcase.waiterservice.exception;

/**
 * Исключение, выбрасываемое, если официант с указанным ID не найден.
 */
public class WaiterNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public WaiterNotFoundException(String message) {
        super(message);
    }
}
