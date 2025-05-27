package com.testcase.kitchenservice.service.impl;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.dto.DishBalanceUpdateDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.entity.Dish;
import com.testcase.kitchenservice.entity.KitchenOrder;
import com.testcase.kitchenservice.entity.OrderToDish;
import com.testcase.kitchenservice.exception.InvalidOrderStatusException;
import com.testcase.kitchenservice.exception.KitchenOrderNotFoundException;
import com.testcase.kitchenservice.kafka.KafkaSender;
import com.testcase.kitchenservice.mapper.KitchenOrderMapper;
import com.testcase.kitchenservice.mapstruct.mapper.KitchenOrderMapstructMapper;
import com.testcase.kitchenservice.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Реализация сервиса обработки кухонных заказов.
 * Осуществляет создание, обновление статусов, валидацию и обмен данными с другими сервисами.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KitchenOrderServiceImpl implements KitchenOrderService {

    private static final String ORDER_NOT_FOUND = "Заказ с id: '%d' не найден";

    private final DishServiceImpl dishServiceImpl;
    private final KitchenOrderMapper kitchenOrderMapper;
    private final KitchenOrderMapstructMapper kitchenOrderMapstructMapper;
    private final KafkaSender kafkaSender;



    @Override
    @Transactional
    public KitchenOrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Обновление статуса заказа с id: {}", orderId);
        KitchenOrder order = kitchenOrderMapper.getKitchenOrderById(orderId)
                .orElseThrow(() -> new KitchenOrderNotFoundException(
                        String.format(ORDER_NOT_FOUND, orderId)));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus resolvedStatus = newStatus;

        if (OrderStatus.COOKING.equals(currentStatus)
                && OrderStatus.CANCELLED_BY_KITCHEN.equals(resolvedStatus)) {
            resolvedStatus = OrderStatus.CANCELLED_WHILE_COOKING_BY_KITCHEN;
        }

        if (!currentStatus.canTransitTo(resolvedStatus)) {
            throw new InvalidOrderStatusException(
                    String.format("Переход из статуса '%s' в '%s' невозможен", currentStatus, resolvedStatus));
        }

        if (OrderStatus.SENT_TO_KITCHEN.equals(currentStatus)
                && OrderStatus.CANCELLED_BY_KITCHEN.equals(resolvedStatus)) {
            Set<DishBalanceUpdateDTO> updates = order.getOrderToDishes().stream()
                    .map(orderToDish -> new DishBalanceUpdateDTO(
                            orderToDish.getDishId(),
                            orderToDish.getDishesCount()))
                    .collect(Collectors.toSet());

            dishServiceImpl.batchUpdateDishBalances(updates);
        }

        if (!OrderStatus.COOKING.equals(currentStatus) && OrderStatus.READY.equals(resolvedStatus)) {
            throw new InvalidOrderStatusException(
                    String.format("Невозможно завершить приготовление заказа с id: '%d' до начала его готовки",
                            order.getKitchenOrderId()));
        }

        order.setStatus(resolvedStatus);
        kitchenOrderMapper.updateStatus(order);
        log.info("Статус заказа с id: {} успешно обновлён на '{}'", orderId, resolvedStatus);

        UpdateOrderStatusDTO statusDTO = new UpdateOrderStatusDTO(orderId, resolvedStatus);
        kafkaSender.sendOrderStatusUpdate(statusDTO);

        return kitchenOrderMapstructMapper.toKitchenOrderDTO(order);
    }

    @Override
    @Transactional
    public void updateOrderStatusFromWaiterServiceByKafka(UpdateOrderStatusDTO statusDTO) {
        log.info("Обновление статуса заказа по сообщению из waiter-service с id: {}", statusDTO.getId());
        OrderStatus newStatus = statusDTO.getStatus();
        KitchenOrder order = kitchenOrderMapper.getKitchenOrderById(statusDTO.getId())
                .orElseThrow(() -> new KitchenOrderNotFoundException(
                        String.format(ORDER_NOT_FOUND, statusDTO.getId())));

        OrderStatus currentStatus = order.getStatus();
        if (OrderStatus.COOKING.equals(currentStatus)
                && OrderStatus.CANCELLED_BY_WAITER.equals(newStatus)) {
            newStatus = OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER;
        }
        if (!currentStatus.canTransitTo(newStatus)) {
            throw new InvalidOrderStatusException(
                    String.format("Переход из статуса '%s' в '%s' невозможен", currentStatus, newStatus));
        }
        if (OrderStatus.SENT_TO_KITCHEN.equals(currentStatus) && OrderStatus.CANCELLED_BY_WAITER.equals(newStatus)) {
            Set<DishBalanceUpdateDTO> updates = order.getOrderToDishes().stream()
                    .map(orderToDish -> new DishBalanceUpdateDTO(
                            orderToDish.getDishId(),
                            orderToDish.getDishesCount()))
                    .collect(Collectors.toSet());

            dishServiceImpl.batchUpdateDishBalances(updates);
        }
        order.setStatus(newStatus);
        kitchenOrderMapper.updateStatus(order);
        log.info("Статус заказа с id: {} по сообщению из waiter-service успешно обновлён на '{}'",
                order.getKitchenOrderId(), newStatus);
    }


    @Override
    public List<KitchenOrderDTO> getKitchenOrdersByCriteria(CriteriaDTO criteriaDTO) {

        log.info("Получение списка заказов по критериям: {}", criteriaDTO.toString());
        List<KitchenOrder> kitchenOrders = kitchenOrderMapper.getKitchenOrdersByCriteria(criteriaDTO);
        log.info("Найдено {} заказов, соответствующих критериям: {}", kitchenOrders.size(), criteriaDTO);
        return kitchenOrderMapstructMapper.toKitchenOrderDTOList(kitchenOrders);
    }


    @Override
    public KitchenOrderDTO getKitchenOrderById(Long id) {
        log.info("Получение заказа с id: {}", id);
        KitchenOrderDTO orderDTO = kitchenOrderMapstructMapper
                .toKitchenOrderDTO(kitchenOrderMapper
                        .getKitchenOrderById(id)
                        .orElseThrow(() -> new KitchenOrderNotFoundException(
                                String.format(ORDER_NOT_FOUND, id))));
        log.info("Найден заказ с id: {}", id);
        return orderDTO;
    }


    @Override
    @Transactional
    public void createKitchenOrderFromOrderDTO(OrderDTO orderDTO) {
        log.info("Создание нового заказа на кухне, полученного из waiter-service с id: {}", orderDTO.getId());

        KitchenOrder kitchenOrder = kitchenOrderMapstructMapper.toKitchenOrder(orderDTO);
        kitchenOrderMapper.insertKitchenOrder(kitchenOrder);

        Set<OrderToDish> orderToDishes = kitchenOrder.getOrderToDishes();
        Set<DishBalanceUpdateDTO> balanceUpdates = new HashSet<>();

        orderToDishes.forEach(orderToDish -> {
            orderToDish.setKitchenOrderId(kitchenOrder.getKitchenOrderId());
            balanceUpdates.add(new DishBalanceUpdateDTO(orderToDish.getDishId(), -orderToDish.getDishesCount()));
        });

        kitchenOrderMapper.batchInsertOrderToDish(orderToDishes);
        log.info("Вставлены {} записей в order_to_dish", orderToDishes.size());

        dishServiceImpl.batchUpdateDishBalances(balanceUpdates);
        log.info("Обновлен баланс блюд в связи с созданием нового заказа на кухне");

        log.info("Новый заказ с id: {} успешно создан", kitchenOrder.getKitchenOrderId());
    }


    @Override
    public Boolean validateOrder(OrderValidationDTO validationDTO) {
        log.info("Валидация позиций заказа из waiter-service с id: {}", validationDTO.getOrderId());
        Set<OrderPositionDTO> positions = validationDTO.getPositions();
        Set<Long> dishIds = positions.stream()
                .filter(Objects::nonNull)
                .map(position -> position.getMenu().getId())
                .collect(Collectors.toSet());

        List<Dish> dishes = dishServiceImpl.getDishesByIds(dishIds);
        List<String> insufficientDishes = new ArrayList<>();

        for (OrderPositionDTO position : positions) {
            Long dishId = position.getMenu().getId();
            Dish validatingDish = dishes.stream()
                    .filter(dish -> dish.getDishId().equals(dishId))
                    .findFirst()
                    .orElse(null);
            if (validatingDish == null) {
                insufficientDishes.add(String.format("Блюдо с id: '%d' не найдено", dishId));
                continue;
            }
            Integer requiredQuantity = position.getQuantity();
            if (validatingDish.getBalance() < requiredQuantity) {
                insufficientDishes.add(String.format(
                        "у блюда с id: '%d' и name: '%s' доступно: %d, требуется: %d",
                        validatingDish.getDishId(), validatingDish.getShortName(),
                        validatingDish.getBalance(), requiredQuantity));
            }
        }

        if (!insufficientDishes.isEmpty()) {
            String message = String.join("; \n", insufficientDishes);
            throw new IllegalArgumentException("Недостаточно блюд: " + message);
        }
        log.info("Валидация позиций заказа из waiter-service с id: {} прошла успешно", validationDTO.getOrderId());
        return true;
    }
}
