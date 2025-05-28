package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.entity.Waiter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper для преобразования сущности {@link Waiter} в DTO {@link WaiterDTO}.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WaiterMapper {

    /**
     * Преобразует объект {@link Waiter} в {@link WaiterDTO}.
     *
     * @param waiter сущность официанта
     * @return {@link WaiterDTO} официанта
     */
    WaiterDTO toWaiterDTO(Waiter waiter);


    /**
     * Преобразует список объектов {@link Waiter} в список DTO {@link WaiterDTO}.
     *
     * @param waiters список сущностей официантов
     * @return список {@link WaiterDTO} официантов
     */
    List<WaiterDTO> toWaiterDTOList(List<Waiter> waiters);
}
