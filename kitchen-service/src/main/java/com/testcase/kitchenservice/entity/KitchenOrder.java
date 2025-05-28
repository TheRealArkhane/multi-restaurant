package com.testcase.kitchenservice.entity;

import com.testcase.commondto.waiterservice.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Сущность, представляющая заказ на кухне.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class KitchenOrder {

    /**
     * Идентификатор заказа на кухне.
     */
    private Long kitchenOrderId;

    /**
     * ID официанта.
     */
    private Long waiterOrderId;

    /**
     * Текущий статус заказа.
     */
    private OrderStatus status;

    /**
     * Дата и время создания заказа.
     */
    private OffsetDateTime createDateTime;

    /**
     * Список блюд в данном заказе.
     */
    private Set<OrderToDish> orderToDishes;
}
