package com.testcase.waiterservice.dto;

import com.testcase.waiterservice.entity.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для передачи информации о платеже")
public class PaymentDTO {

    @Schema(description = "ID платежа (совпадает с ID заказа)", example = "1")
    private Long orderId;

    @Schema(description = "Тип оплаты", example = "CARD")
    private PaymentType paymentType;

    @Schema(description = "Дата и время оплаты", example = "2025-04-22T23:59:59Z")
    private OffsetDateTime paymentDate;

    @Schema(description = "Сумма оплаты", example = "10000.25")
    private Double paymentSum;
}
