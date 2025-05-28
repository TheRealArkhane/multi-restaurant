package com.testcase.waiterservice.controller;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.dto.request.PaymentFilterDTO;
import com.testcase.waiterservice.service.impl.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Оплаты", description = "Операции для управления данными оплат")
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;


    @Operation(summary = "Получить оплату по ID",
            description = "Возвращает информацию о платеже по уникальному идентификатору заказа (ID оплаты).")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Оплата успешно получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Оплата не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping("/{orderId}")
    public PaymentDTO getPaymentById(@PathVariable Long orderId) {
        return paymentServiceImpl.getPaymentById(orderId);
    }


    @Operation(summary = "Получить постраничный список оплат по указанным фильтрам",
            description = "Возвращает список оплат с возможностью фильтрации по типу оплаты, дате и сумме. "
                    + "Параметры: paymentType (тип оплаты), dateFrom (дата от), dateTo (дата до), "
                    + "sumFrom (минимальная сумма), sumTo (максимальная сумма).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Оплаты успешно получены",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PaymentDTO.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @PostMapping("/search")
    public Page<PaymentDTO> getPayments(@Parameter(description = "Фильтры для поиска оплат")
                                        @RequestBody PaymentFilterDTO filter,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return paymentServiceImpl.getPayments(filter, pageable);
    }
}
