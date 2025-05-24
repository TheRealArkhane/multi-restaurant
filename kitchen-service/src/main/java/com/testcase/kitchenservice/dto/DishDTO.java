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
@Schema(description = "DTO для передачи информации о блюде")
public class DishDTO {

    @Schema(description = "ID блюда", example = "1")
    private Long dishId;

    @Schema(description = "Остаток блюда (баланс)", example = "100")
    private Long balance;

    @Schema(description = "Краткое название блюда", example = "Пицца")
    private String shortName;

    @Schema(description = "Состав блюда", example = "Спагетти, томатная паста, говяжий фарш, сыр Пармезан")
    private String dishComposition;
}
