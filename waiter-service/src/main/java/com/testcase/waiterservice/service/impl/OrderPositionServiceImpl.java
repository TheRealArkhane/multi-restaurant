package com.testcase.waiterservice.service.impl;

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
import com.testcase.waiterservice.service.OrderPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация интерфейса сервиса для управления позициями в заказах.
 * Предоставляет методы для создания, обновления, получения и удаления позиций заказа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPositionServiceImpl implements OrderPositionService {

    private static final String ORDER_POSITION_NOT_FOUND = "Позиция с id: '%d' не найдена";

    private final OrderPositionRepository orderPositionRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderPositionMapper orderPositionMapper;


    @Override
    public OrderPosition createOrderPosition(Long menuId, Integer quantity, Long orderId) {
        log.info("Создание позиции заказа: menuId={}, quantity={}, orderId={}", menuId, quantity, orderId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Поле quantity должно быть > 0");
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuPositionNotFoundException(
                        String.format("Позиция меню с id: '%d' не найдена", menuId)));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Заказ с id: '%d' не найден", orderId)));

        OrderPosition orderPosition = orderPositionMapper.setOrderPositionWhileCreating(menu, quantity, order);
        orderPosition = orderPositionRepository.save(orderPosition);
        log.info("Позиция заказа успешно создана: id={}, quantity={}, menuId={}, orderId={}",
                orderPosition.getId(), orderPosition.getQuantity(),
                orderPosition.getMenu().getId(), orderPosition.getOrder().getId());
        return orderPosition;
    }


    @Override
    public OrderPosition getOrderPositionById(Long orderPositionId) {
        log.info("Получение позиции заказа по id: {}", orderPositionId);
        OrderPosition orderPosition = orderPositionRepository.findById(orderPositionId)
                .orElseThrow(() -> new OrderPositionNotFoundException(
                        String.format(ORDER_POSITION_NOT_FOUND, orderPositionId)));
        log.info("Позиция заказа найдена: id={}", orderPosition.getId());
        return orderPosition;
    }


    @Override
    @Transactional
    public void updateOrderPosition(Long orderPositionId, int newQuantity) {
        log.info("Обновление позиции заказа: id={}, newQuantity={}", orderPositionId, newQuantity);
        OrderPosition orderPosition = getOrderPositionById(orderPositionId);
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Поле quantity не может быть отрицательным");
        } else if (newQuantity == 0) {
            deleteOrderPosition(orderPositionId);
            return;
        }
        orderPosition.setQuantity(newQuantity);
        orderPositionRepository.save(orderPosition);
        log.info("Позиция заказа обновлена: id={}, newQuantity={}", orderPositionId, newQuantity);
    }


    @Override
    public void deleteOrderPosition(Long orderPositionId) {
        log.info("Удаление позиции заказа: id: {}", orderPositionId);
        if (!orderPositionRepository.existsById(orderPositionId)) {
            throw new OrderPositionNotFoundException(
                    String.format(ORDER_POSITION_NOT_FOUND, orderPositionId));
        }
        orderPositionRepository.deleteById(orderPositionId);
        log.info("Позиция заказа успешно удалена: id={}", orderPositionId);
    }


    @Override
    public Double getTotalCost(OrderPosition orderPosition) {
        return orderPosition.getMenu().getCost() * orderPosition.getQuantity();
    }
}
