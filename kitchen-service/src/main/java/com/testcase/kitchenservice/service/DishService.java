package com.testcase.kitchenservice.service;

import com.testcase.kitchenservice.dto.DishBalanceUpdateDTO;
import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.entity.Dish;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс сервиса для управления блюдами.
 * Предоставляет методы для получения информации о блюдах и обновления их остатков.
 */
public interface DishService {

    /**
     * Возвращает список всех блюд.
     *
     * @return список DTO блюд {@link DishDTO}
     */
    List<DishDTO> getAllDishes();

    /**
     * Получает DTO блюда по его идентификатору.
     *
     * @param id идентификатор блюда
     * @return DTO блюда {@link DishDTO}
     */
    DishDTO getDishDTOById(Long id);

    /**
     * Получает сущность блюда по его идентификатору.
     *
     * @param id идентификатор блюда
     * @return сущность блюда {@link Dish}
     */
    Dish getDishById(Long id);

    /**
     * Получает список блюд по их идентификаторам.
     *
     * @param ids множество идентификаторов блюд
     * @return список блюд {@link Dish}
     */
    List<Dish> getDishesByIds(Set<Long> ids);

    /**
     * Обновляет остаток блюда на складе.
     *
     * @param id идентификатор блюда
     * @param balance новое значение остатка
     */
    void updateDishBalance(Long id, Integer balance);


    void batchUpdateDishBalances(Set<DishBalanceUpdateDTO> dishUpdates);
}
