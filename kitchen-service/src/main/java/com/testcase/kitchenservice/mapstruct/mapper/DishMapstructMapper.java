package com.testcase.kitchenservice.mapstruct.mapper;

import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.entity.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Маппер для преобразования сущностей {@link Dish} в DTO {@link DishDTO}.
 * Используется для маппинга данных между сущностями блюд и DTO, возвращаемыми во внешние слои.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DishMapstructMapper {

    /**
     * Преобразует сущность {@link Dish} в {@link DishDTO}.
     *
     * @param dish сущность блюда
     * @return DTO блюда {@link DishDTO}
     */
    DishDTO toDishDTO(Dish dish);


    /**
     * Преобразует список сущностей {@link Dish} в список DTO {@link DishDTO}.
     *
     * @param dishList список сущностей блюд
     * @return список DTO блюд {@link DishDTO}
     */
    List<DishDTO> toDishDTOList(List<Dish> dishList);
}
