package com.testcase.waiterservice.service;

import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.exception.OrderNotFoundException;
import com.testcase.waiterservice.exception.OrderPositionNotFoundException;
import com.testcase.waiterservice.mapper.OrderPositionMapper;
import com.testcase.waiterservice.repository.MenuRepository;
import com.testcase.waiterservice.repository.OrderPositionRepository;
import com.testcase.waiterservice.repository.order.OrderRepository;
import com.testcase.waiterservice.service.impl.OrderPositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для {@link OrderPositionServiceImpl}, покрывающие логику создания, получения,
 * обновления и удаления позиций заказа.
 */
@ExtendWith(MockitoExtension.class)
public class OrderPositionServiceImplTest {

    @Mock
    private OrderPositionRepository orderPositionRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderPositionMapper orderPositionMapper;

    @InjectMocks
    private OrderPositionServiceImpl orderPositionServiceImpl;

    private Order order;
    private Menu menu;
    private OrderPosition orderPosition;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);

        menu = new Menu();
        menu.setId(1L);
        menu.setName("Dish 1");
        menu.setCost(10.0);

        orderPosition = new OrderPosition();
        orderPosition.setId(100L);
        orderPosition.setQuantity(2);
        orderPosition.setOrder(order);
        orderPosition.setMenu(menu);
    }


    /**
     * Проверяет успешное создание позиции заказа.
     * given: Существующие ID меню и заказа, настроенные моки репозиториев и маппера.
     * when: Вызывается createOrderPosition с валидными параметрами.
     * then: Позиция сохраняется, проверяется взаимодействие с зависимостями.
     */
    @Test
    @DisplayName("Создание позиции заказа - успешно")
    void createOrderPosition_Success() {
        // given
        Integer quantity = 3;
        Long menuId = menu.getId();
        Long orderId = order.getId();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderPositionMapper.setOrderPositionWhileCreating(menu, quantity, order))
                .thenReturn(orderPosition);
        when(orderPositionRepository.save(orderPosition)).thenReturn(orderPosition);

        // when
        OrderPosition result = orderPositionServiceImpl.createOrderPosition(menuId, quantity, orderId);

        // then
        assertNotNull(result);
        assertEquals(orderPosition.getId(), result.getId());
        verify(menuRepository).findById(menuId);
        verify(orderRepository).findById(orderId);
        verify(orderPositionMapper).setOrderPositionWhileCreating(menu, quantity, order);
        verify(orderPositionRepository).save(orderPosition);
    }


    /**
     * Проверяет обработку отсутствия позиции меню при создании заказа.
     * given: Несуществующий ID меню.
     * when: Вызывается createOrderPosition с невалидным menuId.
     * then: Выбрасывается MenuPositionNotFoundException.
     */
    @Test
    @DisplayName("Создание позиции с несуществующим меню")
    void createOrderPosition_MenuNotFound() {
        // given
        Integer quantity = 2;
        Long menuId = 999L; // несуществующий
        Long orderId = order.getId();

        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // when
        MenuPositionNotFoundException ex = assertThrows(MenuPositionNotFoundException.class, () ->
                orderPositionServiceImpl.createOrderPosition(menuId, quantity, orderId)
        );

        // then
        assertEquals(ex.getMessage(), String.format("Позиция меню с id: '%d' не найдена", menuId));
    }


    /**
     * Проверяет обработку отсутствия заказа при создании позиции.
     * given: Несуществующий ID заказа.
     * when: Вызывается createOrderPosition с невалидным orderId.
     * then: Выбрасывается OrderNotFoundException.
     */
    @Test
    @DisplayName("Создание позиции с несуществующим заказом")
    void createOrderPosition_OrderNotFound() {
        // given
        Integer quantity = 2;
        Long menuId = menu.getId();
        Long orderId = 999L; // несуществующий заказ

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () ->
                orderPositionServiceImpl.createOrderPosition(menuId, quantity, orderId)
        );

        // then
        assertEquals(ex.getMessage(), String.format("Заказ с id: '%d' не найден", orderId));
    }


    /**
     * Проверяет получение существующей позиции заказа.
     * given: Существующий ID позиции заказа.
     * when: Вызывается getOrderPositionById с валидным ID.
     * then: Возвращается корректная сущность позиции.
     */
    @Test
    @DisplayName("Получение позиции заказа по ID - успешно")
    void getOrderPositionById_Success() {
        // given
        Long orderPositionId = orderPosition.getId();
        when(orderPositionRepository.findById(orderPositionId)).thenReturn(Optional.of(orderPosition));

        // when
        OrderPosition result = orderPositionServiceImpl.getOrderPositionById(orderPositionId);

        // then
        assertNotNull(result);
        assertEquals(orderPositionId, result.getId());
        verify(orderPositionRepository).findById(orderPositionId);
    }


    /**
     * Проверяет обработку запроса несуществующей позиции заказа.
     * given: Несуществующий ID позиции.
     * when: Вызывается getOrderPositionById с невалидным ID.
     * then: Выбрасывается OrderPositionNotFoundException.
     */
    @Test
    @DisplayName("Получение несуществующей позиции заказа")
    void getOrderPositionById_OrderPositionNotFound() {
        // given
        Long orderPositionId = 999L; // несуществующий
        when(orderPositionRepository.findById(orderPositionId)).thenReturn(Optional.empty());

        // when
        OrderPositionNotFoundException ex = assertThrows(OrderPositionNotFoundException.class, () ->
                orderPositionServiceImpl.getOrderPositionById(orderPositionId)
        );

        // then
        assertEquals(ex.getMessage(), String.format("Позиция с id: '%d' не найдена", orderPositionId));
    }


    /**
     * Проверяет обновление количества блюд в позиции заказа.
     * given: Существующая позиция и новое валидное количество.
     * when: Вызывается updateOrderPosition с новым quantity.
     * then: Количество обновляется, проверяется сохранение изменений.
     */
    @Test
    @DisplayName("Обновление количества в позиции заказа")
    void updateOrderPosition_Success() {
        // given
        int newQuantity = 5;
        Long orderPositionId = orderPosition.getId();

        when(orderPositionRepository.findById(orderPositionId)).thenReturn(Optional.of(orderPosition));
        when(orderPositionRepository.save(orderPosition)).thenReturn(orderPosition);

        // when
        orderPositionServiceImpl.updateOrderPosition(orderPositionId, newQuantity);

        // then
        assertEquals(newQuantity, orderPosition.getQuantity());
        verify(orderPositionRepository).save(orderPosition);
    }


    /**
     * Проверяет обработку отрицательного количества при обновлении.
     * given: Отрицательное значение quantity.
     * when: Вызывается updateOrderPosition с отрицательным значением.
     * then: Выбрасывается IllegalArgumentException.
     */
    @Test
    @DisplayName("Обновление с отрицательным количеством")
    void updateOrderPosition_NegativeQuantity() {
        // given
        int newQuantity = -3;
        Long orderPositionId = orderPosition.getId();

        when(orderPositionRepository.findById(orderPositionId)).thenReturn(Optional.of(orderPosition));

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                orderPositionServiceImpl.updateOrderPosition(orderPositionId, newQuantity)
        );

        // then
        assertEquals("Поле quantity не может быть отрицательным", ex.getMessage());
    }


    /**
     * Проверяет успешное удаление позиции заказа.
     * given: Существующий ID позиции заказа.
     * when: Вызывается deleteOrderPosition с валидным ID.
     * then: Позиция удаляется, проверяется вызов репозитория.
     */
    @Test
    @DisplayName("Удаление позиции заказа - успешно")
    void deleteOrderPosition_Success() {
        // given
        Long orderPositionId = orderPosition.getId();
        when(orderPositionRepository.existsById(orderPositionId)).thenReturn(true);

        // when
        orderPositionServiceImpl.deleteOrderPosition(orderPositionId);

        // then
        verify(orderPositionRepository).deleteById(orderPositionId);
    }
}