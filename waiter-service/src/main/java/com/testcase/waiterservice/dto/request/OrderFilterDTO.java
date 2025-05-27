package com.testcase.waiterservice.dto.request;

import com.testcase.commondto.waiterservice.OrderStatus;
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
@Schema(description = "Фильтры для поиска заказов")
public class OrderFilterDTO {

    @Schema(description = "Статус заказа", example = "PAID_AND_SERVED")
    private OrderStatus status;

    @Schema(description = "Дата от (включительно)", example = "2025-04-01T00:00:00Z")
    private OffsetDateTime createdFrom;

    @Schema(description = "Дата до (включительно)", example = "2025-04-21T23:59:59Z")
    private OffsetDateTime createdTo;

    @Schema(description = "ID официанта", example = "1")
    private Long waiterId;

    @Schema(description = "Номер стола", example = "A12")
    private String tableNumber;
}
