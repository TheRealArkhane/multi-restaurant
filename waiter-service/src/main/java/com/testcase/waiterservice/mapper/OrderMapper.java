package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.Waiter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущности {@link Order} в DTO {@link OrderDTO} и создания новой сущности заказа.
 * Использует {@link WaiterMapper} и {@link OrderPositionMapper} для маппинга связанных объектов.
 * Реализует маппинг для основных операций преобразования данных.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {WaiterMapper.class, OrderPositionMapper.class})
public interface OrderMapper {

    /**
     * Преобразует объект {@link Order} в {@link OrderDTO}.
     *
     * @param order сущность заказа
     * @return {@link OrderDTO} заказа
     */
    OrderDTO toOrderDTO(Order order);

    /**
     * Преобразует список объектов {@link Order} в список DTO {@link OrderDTO}.
     *
     * @param orders список сущностей заказа
     * @return список {@link OrderDTO} заказа
     */
    List<OrderDTO> toOrderDTOList(List<Order> orders);

    /**
     * Преобразует объект {@link OrderCreateRequestDTO} в сущность {@link Order}.
     * <p>
     * При создании новой сущности:
     * <ul>
     *     <li>Поле id игнорируется (будет сгенерировано базой данных).</li>
     *     <li>Устанавливается дефолтное значение для поля status равное "CREATED".</li>
     *     <li>Поле createDateTime устанавливается текущим временем.</li>
     * </ul>
     * </p>
     *
     * @param requestDTO DTO с данными для создания заказа
     * @param waiter     сущность официанта, связанного с заказом
     * @return новая сущность заказа {@link Order}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PREPARING")
    @Mapping(target = "createDateTime", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "orderPositions", ignore = true)
    Order setOrderWhileCreating(OrderCreateRequestDTO requestDTO, Waiter waiter);
}
