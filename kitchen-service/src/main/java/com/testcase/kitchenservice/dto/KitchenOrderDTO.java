package com.testcase.kitchenservice.dto;

import com.testcase.commondto.waiterservice.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для передачи информации о заказе кухни")
public class KitchenOrderDTO {

    @Schema(description = "ID заказа кухни", example = "1")
    private Long kitchenOrderId;

    @Schema(description = "ID официанта", example = "1")
    private Long waiterOrderId;

    @Schema(description = "Статус заказа", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "Дата и время создания заказа", example = "2025-04-06T21:40:36.775Z")
    private OffsetDateTime createDateTime;

    @Schema(description = "Список позиций заказа")
    private Set<OrderToDishDTO> orderToDishes;
}
