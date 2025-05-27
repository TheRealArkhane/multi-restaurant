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
@Schema(description = "DTO для блюда из меню")
public class MenuDTO {

    @Schema(description = "ID блюда", example = "1")
    private Long id;

    @Schema(description = "Название блюда", example = "Пицца")
    private String name;

    @Schema(description = "Стоимость блюда", example = "12.5")
    private Double cost;
}
