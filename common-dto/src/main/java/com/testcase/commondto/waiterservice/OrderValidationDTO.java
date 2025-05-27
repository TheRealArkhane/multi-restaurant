package com.testcase.commondto.waiterservice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для валидации наличия позиций заказа из сервиса официантов в сервисе кухни")
public class OrderValidationDTO {

    @Schema(description = "ID заказа")
    private Long orderId;

    @Schema(description = "Список позиций заказа")
    private Set<OrderPositionDTO> positions;
}
