package com.testcase.kitchenservice.dto;

import com.education.commondto.waiterservice.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "DTO для передачи критериев поиска заказов")
public class CriteriaDTO {

    @Schema(description = "Статус заказа.", example = "READY")
    private OrderStatus status;

    @Schema(description = "Дата начала создания заказов (включительно)", example = "2025-04-10T00:00:00Z")
    private OffsetDateTime createdFrom;

    @Schema(description = "Дата окончания создания заказов (включительно)", example = "2025-04-25T23:59:59Z")
    private OffsetDateTime createdTo;

    @Schema(description = "ID официанта, который создал заказ", example = "1")
    private Long waiterId;
}
