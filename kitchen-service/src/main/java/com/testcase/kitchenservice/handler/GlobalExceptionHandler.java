package com.testcase.kitchenservice.handler;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.kitchenservice.exception.DishNotFoundException;
import com.testcase.kitchenservice.exception.InvalidOrderStatusException;
import com.testcase.kitchenservice.exception.KitchenOrderNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для сервиса кухни.
 * Обрабатывает специализированные и прочие исключения, возвращая структурированный ответ с информацией об ошибке.
 */
@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link InvalidOrderStatusException}, возникающее при некорректной смене статуса заказа.
     *
     * @param ex исключение
     * @return ответ с кодом 400 Bad Request и сообщением об ошибке
     */
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidOrderStatusException(InvalidOrderStatusException ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDTO(ex.getMessage()));
    }

    /**
     * Обрабатывает исключения {@link DishNotFoundException} и {@link KitchenOrderNotFoundException},
     * возникающие при отсутствии блюда или заказа.
     *
     * @param ex исключение DishNotFoundException или KitchenOrderNotFoundException
     * @return ResponseEntity с кодом 404 Not Found и сообщением об ошибке
     */
    @ExceptionHandler({
            DishNotFoundException.class,
            KitchenOrderNotFoundException.class
    })
    public ResponseEntity<ErrorMessageDTO> handleNotFoundException(RuntimeException ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(ex.getMessage()));
    }

    /**
     * Обрабатывает все остальные необработанные исключения.
     *
     * @param ex исключение
     * @return ответ с кодом 500 Internal Server Error и сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGeneralException(Exception ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO("Unexpected exception: " + ex.getMessage()));
    }
}
