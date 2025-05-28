package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущности {@link Menu} в DTO {@link MenuDTO}.
 * Осуществляет маппинг одного меню и списка меню.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuMapper {

    /**
     * Преобразует объект {@link Menu} в {@link MenuDTO}.
     *
     * @param menu сущность меню
     * @return {@link MenuDTO} меню
     */
    MenuDTO toMenuDTO(Menu menu);

    /**
     * Преобразует список объектов {@link Menu} в список DTO {@link MenuDTO}.
     *
     * @param menus список сущностей меню
     * @return список {@link MenuDTO} меню
     */
    List<MenuDTO> toMenuDTOList(List<Menu> menus);
}
