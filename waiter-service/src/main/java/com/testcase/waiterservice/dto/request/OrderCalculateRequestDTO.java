package com.testcase.waiterservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для подсчета позиций заказа")
public class OrderCalculateRequestDTO {

    @NotNull(message = "menuId не может быть null")
    @Min(value = 1, message = "ID официанта должно быть положительным числом.")
    @Schema(description = "ID блюда", example = "1")
    private Long menuId;

    @NotNull(message = "quantity не может быть null")
    @Digits(integer = 2, fraction = 0, message = "quantity должно быть двухзначным числом.")
    @Schema(description = "Количество блюда", example = "1")
    private Integer quantity;
}
