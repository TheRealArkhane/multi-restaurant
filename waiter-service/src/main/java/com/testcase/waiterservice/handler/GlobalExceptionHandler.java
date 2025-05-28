package com.testcase.waiterservice.handler;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.exception.OrderNotFoundException;
import com.testcase.waiterservice.exception.OrderPositionNotFoundException;
import com.testcase.waiterservice.exception.PaymentNotFoundException;
import com.testcase.waiterservice.exception.UnsuccessfulPaymentException;
import com.testcase.waiterservice.exception.WaiterNotFoundException;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для сервиса официантов.
 * Этот класс перехватывает и обрабатывает различные исключения, возникающие в приложении,
 * и возвращает соответствующие ответы с информацией об ошибках в структурированном формате.
 * Используется для централизованной обработки исключений, чтобы облегчить управление ошибками и улучшить поддержку API.
 */
@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {


    /**
     * Обрабатывает исключения, связанные с отсутствием сущностей в БД
     * (например, заказ, официант или позиция в меню не найдены).
     *
     * @param ex исключение типа {@link OrderNotFoundException}, {@link WaiterNotFoundException},
     *           {@link MenuPositionNotFoundException}, {@link OrderPositionNotFoundException}
     * @return ответ с кодом 404 Not Found и сообщением об ошибке
     */
    @ExceptionHandler({
            OrderNotFoundException.class,
            WaiterNotFoundException.class,
            MenuPositionNotFoundException.class,
            OrderPositionNotFoundException.class,
            PaymentNotFoundException.class
    })
    public ResponseEntity<ErrorMessageDTO> handleNotFoundException(RuntimeException ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDTO(ex.getMessage()));
    }


    /**
     * Обрабатывает исключения, возникающие при нарушении валидации аргументов.
     * Возвращает список ошибок валидации.
     *
     * @param ex исключение {@link MethodArgumentNotValidException}, которое возникает при ошибках валидации
     * @return список ошибок валидации с кодом 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        List<ErrorMessageDTO> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .map(ErrorMessageDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }


    /**
     * Обрабатывает исключения, возникающие при вызове внешних Feign-сервисов.
     *
     * @param ex исключение {@link FeignException}, возникающее при ошибке при вызове внешнего сервиса
     * @return ответ с кодом 500 Internal Server Error и сообщением о проблеме при вызове удаленного сервиса
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessageDTO> handleFeignException(FeignException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO("Ошибка при вызове удаленного сервиса: " + ex.getMessage()));
    }


    /**
     * Обрабатывает исключения, связанные с ошибками в аргументах или состояниях.
     *
     * @param ex исключение {@link IllegalArgumentException} или {@link IllegalStateException}
     * @return ответ с кодом 400 Bad Request и сообщением об ошибке
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorMessageDTO> handleIllegalArgumentAndStateException(RuntimeException ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(ex.getMessage()));
    }


    /**
     * Обрабатывает исключения, связанные с неудачной оплатой.
     *
     * @param ex исключение {@link UnsuccessfulPaymentException}, возникающее при неудачной оплате
     * @return ответ с кодом 401 Unauthorized и сообщением о проблемах с оплатой
     */
    @ExceptionHandler(UnsuccessfulPaymentException.class)
    public ResponseEntity<ErrorMessageDTO> handleUnsuccessfulPaymentException(UnsuccessfulPaymentException ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessageDTO(ex.getMessage()));
    }


    /**
     * Обрабатывает все остальные необработанные исключения.
     *
     * @param ex общее исключение
     * @return ответ с кодом 500 Internal Server Error и сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGeneralException(Exception ex) {
        log.error("stack trace: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDTO("Unexpected exception: " + ex.getMessage()));
    }
}
