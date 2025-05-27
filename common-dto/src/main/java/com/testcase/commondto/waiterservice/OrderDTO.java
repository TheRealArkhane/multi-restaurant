package com.testcase.commondto.waiterservice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обмена данными заказа между сервисом кухни и сервисом официантов")
public class OrderDTO {

    @Schema(description = "ID заказа", example = "1")
    private Long id;

    @Schema(description = "Статус заказа", example = "SENT_TO_KITCHEN")
    private OrderStatus status;

    @Schema(description = "Дата и время создания заказа", example = "2025-04-06T21:40:36.775Z")
    private OffsetDateTime createDateTime;

    @Schema(description = "Информация об официанте")
    private WaiterDTO waiter;

    @Schema(description = "Номер стола", example = "A12")
    private String tableNumber;

    @Schema(description = "Позиции заказа")
    private Set<OrderPositionDTO> orderPositions;
}
