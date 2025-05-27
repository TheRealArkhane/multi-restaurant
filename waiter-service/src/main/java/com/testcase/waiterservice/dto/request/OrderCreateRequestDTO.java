package com.testcase.waiterservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания нового заказа")
public class OrderCreateRequestDTO {

    @NotNull(message = "ID официанта не может быть null")
    @Min(value = 1, message = "ID официанта должно быть положительным числом")
    @Schema(description = "ID официанта", example = "1")
    private Long waiterId;

    @NotBlank(message = "Номер стола не может быть пустым или null")
    @Schema(description = "Номер стола", example = "A12")
    private String tableNumber;
}
