package com.testcase.commondto;

import com.testcase.commondto.waiterservice.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обновления статуса заказа")
public class UpdateOrderStatusDTO {

    @NotNull(message = "ID заказа не может быть null")
    @Digits(integer = 10, fraction = 0, message = "ID заказа должно быть числовым значением до 10 знаков.")
    @Min(value = 1, message = "ID заказа должно быть положительным числом.")
    @Schema(description = "ID заказа", example = "1")
    private Long id;

    @NotNull(message = "ID заказа не может быть null")
    @Schema(description = "Новый статус заказа", example = "READY")
    private OrderStatus status;
}
