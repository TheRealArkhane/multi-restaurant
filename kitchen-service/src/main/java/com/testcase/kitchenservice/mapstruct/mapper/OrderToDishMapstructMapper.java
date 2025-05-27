package com.testcase.kitchenservice.mapstruct.mapper;

import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.kitchenservice.dto.OrderToDishDTO;
import com.testcase.kitchenservice.entity.OrderToDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущностей {@link OrderToDish}
 * в соответствующие DTO {@link OrderToDishDTO}.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderToDishMapstructMapper {

    /**
     * Преобразует сущность {@link OrderToDish} в {@link OrderToDishDTO}.
     *
     * @param orderToDish позиция заказа, связывающая блюдо с заказом
     * @return {@link OrderToDishDTO} позиции заказа
     */
    OrderToDishDTO toOrderToDishDTO(OrderToDish orderToDish);


    /**
     * Преобразует список сущностей {@link OrderToDish} в список {@link OrderToDishDTO}.
     *
     * @param orderToDishes список позиций заказа
     * @return список {@link OrderToDishDTO} позиций заказа
     */
    List<OrderToDishDTO> toOrderToDishDTOList(List<OrderToDish> orderToDishes);


    /**
     * Преобразует DTO позиции заказа {@link OrderPositionDTO} в сущность {@link OrderToDish}.
     *
     * @param dto DTO позиции заказа
     * @return сущность {@link OrderToDish}
     */
    @Mapping(source = "orderId", target = "kitchenOrderId")
    @Mapping(source = "menu.id", target = "dishId")
    @Mapping(source = "quantity", target = "dishesCount")
    OrderToDish toOrderToDish(OrderPositionDTO dto);


    /**
     * Преобразует список DTO {@link OrderPositionDTO} в список сущностей {@link OrderToDish}.
     *
     * @param dtoList список DTO позиций заказа
     * @return список сущностей {@link OrderToDish}
     */
    List<OrderToDish> toOrderToDishes(List<OrderPositionDTO> dtoList);
}
