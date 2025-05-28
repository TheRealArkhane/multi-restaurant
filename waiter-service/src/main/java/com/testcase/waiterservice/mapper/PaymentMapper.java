package com.testcase.waiterservice.mapper;

import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.Payment;
import com.testcase.waiterservice.entity.PaymentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper для преобразования сущности {@link Payment} в DTO {@link PaymentDTO}.
 * Используется Spring ComponentModel для интеграции в Spring Context.
 * Этот маппер необходим для преобразования данных о платеже между сущностью и представлением.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    /**
     * Преобразует объект {@link Payment} в {@link PaymentDTO}.
     *
     * @param payment сущность платежа
     * @return {@link PaymentDTO} платежа
     */
    PaymentDTO toPaymentDTO(Payment payment);


    /**
     * Устанавливает параметры для создания нового объекта {@link Payment}.
     *
     * @param order заказ, с которым связан платеж
     * @param paymentType тип платежа
     * @param paymentSum сумма платежа
     * @return объект {@link Payment}, готовый для сохранения
     */
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "paymentType", source = "paymentType")
    @Mapping(target = "paymentDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "paymentSum", source = "paymentSum")
    @Mapping(target = "order", source = "order")
    Payment setPaymentParameters(Order order, PaymentType paymentType, Double paymentSum);
}
