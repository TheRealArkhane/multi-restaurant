package com.testcase.kitchenservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для передачи информации о позиции заказа кухни")
public class OrderToDishDTO {

    @Schema(description = "ID заказа кухни", example = "1")
    private Long kitchenOrderId;

    @Schema(description = "ID блюда", example = "1")
    private Long dishId;

    @Schema(description = "Количество блюда в позиции заказа", example = "1")
    private Integer dishesCount;
}
