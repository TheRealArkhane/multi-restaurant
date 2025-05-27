package com.testcase.commondto.waiterservice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для позиции заказа")
public class OrderPositionDTO {

    @Schema(description = "ID заказа", example = "1")
    private Long orderId;

    @Schema(description = "Количество позиций", example = "1")
    private Integer quantity;

    @Schema(description = "Информация о блюде из меню")
    private MenuDTO menu;
}
