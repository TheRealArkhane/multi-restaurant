package com.testcase.kitchenservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность, представляющая блюдо, доступное на кухне.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Dish {

    /**
     * ID блюда.
     */
    private Long dishId;

    /**
     * Остаток блюда на складе.
     */
    private Integer balance;

    /**
     * Краткое название блюда.
     */
    private String shortName;

    /**
     * Состав блюда (ингредиенты).
     */
    private String dishComposition;
}
