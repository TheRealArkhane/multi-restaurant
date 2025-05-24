package com.testcase.kitchenservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность, представляющая связь между заказом на кухне и блюдом.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OrderToDish {

    /**
     * ID заказа на кухне.
     */
    private Long kitchenOrderId;

    /**
     * ID блюда.
     */
    private Long dishId;

    /**
     * Количество блюд в заказе.
     */
    private Integer dishesCount;
}
