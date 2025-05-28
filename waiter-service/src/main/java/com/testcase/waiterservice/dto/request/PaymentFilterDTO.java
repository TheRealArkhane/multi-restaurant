package com.testcase.waiterservice.dto.request;

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
@Schema(description = "Фильтры для поиска платежей")
public class PaymentFilterDTO {

    @Schema(description = "Тип оплаты", example = "CARD")
    private PaymentType paymentType;

    @Schema(description = "Дата оплаты от (включительно)", example = "2025-04-01T00:00:00Z")
    private OffsetDateTime dateFrom;

    @Schema(description = "Дата оплаты до (включительно)", example = "2025-04-21T23:59:59Z")
    private OffsetDateTime dateTo;

    @Schema(description = "Минимальная сумма оплаты", example = "1000.00")
    private Double sumFrom;

    @Schema(description = "Максимальная сумма оплаты", example = "5000.00")
    private Double sumTo;
}
