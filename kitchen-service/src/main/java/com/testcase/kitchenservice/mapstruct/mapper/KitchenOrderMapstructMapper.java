package com.testcase.kitchenservice.mapstruct.mapper;

import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.entity.KitchenOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущностей {@link KitchenOrder}
 * и DTO {@link KitchenOrderDTO}, {@link OrderDTO}, {@link UpdateOrderStatusDTO}.
 * Используется для маппинга данных между слоем кухни и внешними DTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = OrderToDishMapstructMapper.class)
public interface KitchenOrderMapstructMapper {

    /**
     * Преобразует сущность {@link KitchenOrder} в DTO {@link KitchenOrderDTO}.
     *
     * @param order сущность заказа на кухне
     * @return {@link KitchenOrderDTO} заказа на кухне
     */
    KitchenOrderDTO toKitchenOrderDTO(KitchenOrder order);


    /**
     * Преобразует список заказов кухни в список {@link KitchenOrderDTO}.
     *
     * @param orders список заказов
     * @return список {@link KitchenOrderDTO} заказов
     */
    List<KitchenOrderDTO> toKitchenOrderDTOList(List<KitchenOrder> orders);


    /**
     * Преобразует сущность {@link KitchenOrder} в DTO для обновления статуса {@link UpdateOrderStatusDTO}.
     * Используется при обратной отправке информации от кухни официанту.
     *
     * @param kitchenOrder заказ на кухне
     * @return {@link UpdateOrderStatusDTO} обновления статуса заказа
     */
    @Mapping(source = "kitchenOrderId", target = "id")
    UpdateOrderStatusDTO toUpdateOrderStatusDTO(KitchenOrder kitchenOrder);


    /**
     * Преобразует внешний заказ {@link OrderDTO}, полученный от сервиса официантов,
     * во внутреннюю сущность {@link KitchenOrder}.
     *
     * @param orderDTO DTO заказа, полученного от waiter-service
     * @return сущность {@link KitchenOrder}
     */
    @Mapping(source = "id", target = "kitchenOrderId")
    @Mapping(source = "waiter.id", target = "waiterOrderId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createDateTime", target = "createDateTime")
    @Mapping(source = "orderPositions", target = "orderToDishes")
    KitchenOrder toKitchenOrder(OrderDTO orderDTO);
}
