package com.testcase.waiterservice.mapper;

import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.Payment;
import com.testcase.waiterservice.entity.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки маппера {@link PaymentMapper}.
 * Тесты проверяют корректность преобразования объекта {@link Payment} в {@link PaymentDTO}
 * и установки параметров при создании объекта {@link Payment}.
 */
public class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    /**
     * Проверяет успешное преобразование объекта Payment в PaymentDTO.
     * given: Создан объект Payment с заданными параметрами.
     * when: Вызывается метод toPaymentDTO.
     * then: Проверяется соответствие всех полей DTO исходному объекту.
     */
    @Test
    @DisplayName("Преобразование Payment в PaymentDTO")
    void toPaymentDTO_Success() {
        // given
        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setPaymentType(PaymentType.CARD);
        payment.setPaymentDate(OffsetDateTime.parse("2025-04-22T23:59:59Z"));
        payment.setPaymentSum(10000.25);

        Order order = new Order();
        order.setId(1L);
        payment.setOrder(order);

        // when
        PaymentDTO paymentDTO = paymentMapper.toPaymentDTO(payment);

        // then
        assertNotNull(paymentDTO, "DTO не должен быть null");
        assertEquals(payment.getOrderId(), paymentDTO.getOrderId(), "ID заказа должен совпадать");
        assertEquals(payment.getPaymentType(), paymentDTO.getPaymentType(), "Тип оплаты должен совпадать");
        assertEquals(payment.getPaymentDate(), paymentDTO.getPaymentDate(), "Дата оплаты должна совпадать");
        assertEquals(payment.getPaymentSum(), paymentDTO.getPaymentSum(), "Сумма оплаты должна совпадать");
    }

    /**
     * Проверяет корректность установки параметров при создании объекта Payment.
     * given: Параметры для создания Payment (заказ, тип оплаты, сумма).
     * when: Вызывается метод setPaymentParameters.
     * then: Проверяется заполнение полей и автоматическая установка даты оплаты.
     */
    @Test
    @DisplayName("Установка параметров для создания Payment")
    void setPaymentParameters_Success() {
        // given
        Order order = new Order();
        order.setId(1L);
        PaymentType paymentType = PaymentType.CASH;
        Double paymentSum = 1500.0;

        // when
        Payment payment = paymentMapper.setPaymentParameters(order, paymentType, paymentSum);

        // then
        assertNotNull(payment, "Объект Payment не должен быть null");
        assertEquals(paymentType, payment.getPaymentType(), "Тип оплаты должен быть установлен");
        assertEquals(paymentSum, payment.getPaymentSum(), "Сумма оплаты должна быть установлена");
        assertEquals(order, payment.getOrder(), "Связанный заказ должен быть установлен");

        // Проверка с допуском ±1 секунда для времени оплаты
        OffsetDateTime now = OffsetDateTime.now();
        assertTrue(payment.getPaymentDate().isAfter(now.minusSeconds(1))
                        && payment.getPaymentDate().isBefore(now.plusSeconds(1)),
                "Дата оплаты должна соответствовать текущему времени"
        );
    }
}
