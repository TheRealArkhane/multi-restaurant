package com.testcase.kitchenservice.mapper;

import com.testcase.kitchenservice.dto.DishBalanceUpdateDTO;
import com.testcase.kitchenservice.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * MyBatis-маршрутизатор для работы с сущностью {@link Dish}.
 * Определяет методы для получения блюд, обновления их остатков и извлечения полного списка.
 */
@Mapper
@Repository
public interface DishMapper {

    /**
     * Получает блюдо по его идентификатору.
     *
     * @param id идентификатор блюда
     * @return {@link Optional} с найденным блюдом или пустой, если блюдо не найдено
     */
    Optional<Dish> getDishById(@Param("id") Long id);


    /**
     * Получает список всех блюд из базы данных.
     *
     * @return список сущностей {@link Dish}
     */
    List<Dish> getAllDishes();


    /**
     * Получает список блюд по заданному множеству ID.
     *
     * @param dishIds множество идентификаторов блюд
     * @return список блюд с указанными идентификаторами
     */
    List<Dish> getDishesByIds(@Param("dishIds") Set<Long> dishIds);


    /**
     * Обновляет остаток блюда по его идентификатору.
     *
     * @param id идентификатор блюда
     * @param additionalValue добавочное значение для блюда
     * @return количество обновлённых записей
     */
    int updateDishBalance(@Param("id") Long id, @Param("additionalValue") Integer additionalValue);


    void batchUpdateDishBalances(@Param("set") Set<DishBalanceUpdateDTO> dishUpdates);
}
