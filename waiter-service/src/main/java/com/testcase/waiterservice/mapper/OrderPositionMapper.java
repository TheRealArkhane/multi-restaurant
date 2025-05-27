package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущности {@link OrderPosition} в DTO {@link OrderPositionDTO}.
 * Использует {@link MenuMapper} для маппинга связанных меню.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {MenuMapper.class})
public interface OrderPositionMapper {

    /**
     * Преобразует объект {@link OrderPosition} в {@link OrderPositionDTO}.
     * При этом, идентификатор заказа устанавливается из свойства order.id.
     *
     * @param orderPosition сущность позиции заказа
     * @return {@link OrderPositionDTO} позиции заказа
     */
    @Mapping(source = "order.id", target = "orderId")
    OrderPositionDTO toOrderPositionDTO(OrderPosition orderPosition);

    /**
     * Преобразует список объектов {@link OrderPosition} в список DTO {@link OrderPositionDTO}.
     *
     * @param orderPositions список сущностей позиций заказа
     * @return список {@link OrderPositionDTO} позиций заказа
     */
    List<OrderPositionDTO> toOrderPositionDTOList(List<OrderPosition> orderPositions);


    /**
     * Устанавливает параметры для создания нового объекта {@link OrderPosition}.
     *
     * @param menu меню, к которому относится позиция заказа
     * @param quantity количество заказанных позиций
     * @param order заказ, к которому принадлежит позиция
     * @return объект {@link OrderPosition}, готовый для сохранения
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menu", source = "menu")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "order", source = "order")
    OrderPosition setOrderPositionWhileCreating(Menu menu, Integer quantity, Order order);
}
