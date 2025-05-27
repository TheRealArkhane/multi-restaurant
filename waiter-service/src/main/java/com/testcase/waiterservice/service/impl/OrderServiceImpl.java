package com.testcase.waiterservice.service.impl;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.waiterservice.client.ValidationClient;
import com.testcase.waiterservice.dto.request.OrderCalculateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderFilterDTO;
import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import com.testcase.waiterservice.entity.Payment;
import com.testcase.waiterservice.entity.PaymentType;
import com.testcase.waiterservice.entity.Waiter;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.exception.OrderNotFoundException;
import com.testcase.waiterservice.exception.OrderServingException;
import com.testcase.waiterservice.exception.WaiterNotFoundException;
import com.testcase.waiterservice.kafka.KafkaSender;
import com.testcase.waiterservice.mapper.OrderMapper;
import com.testcase.waiterservice.mapper.PaymentMapper;
import com.testcase.waiterservice.repository.MenuRepository;
import com.testcase.waiterservice.repository.order.OrderRepository;
import com.testcase.waiterservice.repository.payment.PaymentRepository;
import com.testcase.waiterservice.repository.WaiterRepository;
import com.testcase.waiterservice.repository.order.OrderSpecification;
import com.testcase.waiterservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Реализация сервиса управления заказами.
 * Содержит логику создания, расчёта, обновления, отмены заказов,
 * а также обработки платежей и отправки сообщений в Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NOT_FOUND = "Заказ с id: '%d' не найден";
    private static final String CANCEL_SUCCESS_MESSAGE = "Заказ с id: {} успешно отменён. Статус: {}";

    private final OrderRepository orderRepository;
    private final WaiterRepository waiterRepository;
    private final MenuRepository menuRepository;
    private final PaymentRepository paymentRepository;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;
    private final OrderPositionServiceImpl orderPositionServiceImpl;
    private final ValidationClient validationClient;
    private final KafkaSender kafkaSender;


    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Получение всех заказов");
        List<OrderDTO> orders = orderMapper.toOrderDTOList(orderRepository.findAll());
        log.info("Получено {} заказов", orders.size());
        return orders;
    }


    @Override
    public OrderDTO getOrderById(Long id) {
        log.info("Получение заказа с id: {}", id);
        OrderDTO orderDTO = orderMapper.toOrderDTO(orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ORDER_NOT_FOUND, id))));
        log.info("Заказ с id: {} получен", id);
        return orderDTO;
    }


    @Override
    public OrderDTO createOrder(OrderCreateRequestDTO requestDTO) {
        log.info("Создание нового заказа по запросу: {}", requestDTO);
        Waiter waiter = waiterRepository.findById(requestDTO.getWaiterId())
                .orElseThrow(() -> new WaiterNotFoundException(
                        String.format("Официант с id: %d не найден", requestDTO.getWaiterId())));
        Order newOrder = orderMapper.setOrderWhileCreating(requestDTO, waiter);
        Order savedOrder = orderRepository.save(newOrder);
        log.info("Заказ успешно создан с id: {}", savedOrder.getId());
        return orderMapper.toOrderDTO(savedOrder);
    }


    @Override
    @Transactional
    public OrderDTO calculateOrder(Long orderId, OrderCalculateRequestDTO requestDTO) {
        log.info("Подсчет заказа с id: {}, позиции: {}", orderId, requestDTO);
        int quantity = requestDTO.getQuantity();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new OrderNotFoundException(
                                String.format(ORDER_NOT_FOUND, orderId)));

        if (!OrderStatus.PREPARING.equals(order.getStatus())) {
            throw new IllegalStateException(
                    String.format("Невозможно произвести подсчет заказа со статусом: %s, требуется %s",
                            order.getStatus(), OrderStatus.PREPARING));
        }

        Menu menu = menuRepository.findById(requestDTO.getMenuId())
                .orElseThrow(() -> new MenuPositionNotFoundException(
                        String.format("Позиция меню с id: '%d' не найдена", requestDTO.getMenuId())));

        Optional<OrderPosition> existingPosition =
                order.getOrderPositions()
                        .stream()
                        .filter(position -> position.getMenu().equals(menu))
                        .findFirst();

        if (existingPosition.isPresent()) {
            int existingPositionQuantitySum = existingPosition.get().getQuantity() + quantity;
            if (existingPositionQuantitySum <= 0) {
                removePosition(order, existingPosition.get());
            } else {
                orderPositionServiceImpl.updateOrderPosition(
                        existingPosition.get().getId(),
                        existingPositionQuantitySum);
            }
        } else {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Поле quantity должно быть > 0");
            }
            OrderPosition orderPosition = orderPositionServiceImpl.createOrderPosition(menu.getId(), quantity, orderId);
            addPosition(order, orderPosition);
        }
        orderRepository.save(order);
        log.info("Заказ с id: {} успешно обновлён после подсчёта", orderId);
        return orderMapper.toOrderDTO(order);
    }


    @Override
    public void clearOrderPositions(Long orderId) {
        log.info("Очистка всех позиций заказа с id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new OrderNotFoundException(
                                String.format(ORDER_NOT_FOUND, orderId)));

        if (OrderStatus.PREPARING.equals(order.getStatus())
                || OrderStatus.CANCELLED_BEFORE_SEND.equals(order.getStatus())) {
            order.getOrderPositions().clear();
            orderRepository.save(order);
            log.info("Все позиции заказа с id: {} удалены", orderId);
        } else {
            throw new IllegalStateException(
                    String.format("Невозможно удалить позиции заказа со статусом: %s, требуется %s или %s",
                            order.getStatus(), OrderStatus.PREPARING, OrderStatus.CANCELLED_BEFORE_SEND));
        }
    }


    @Override
    public OrderDTO sendOrderToKitchen(Long orderId) {
        log.info("Отправка заказа с id: {} в сервис кухни через Kafka", orderId);
        OrderDTO orderDTO = getOrderById(orderId);
        if (orderDTO.getOrderPositions().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Заказ с id: '%d' не должен быть пустым", orderId));
        }
        if (!OrderStatus.PREPARING.equals(orderDTO.getStatus())) {
            throw new IllegalStateException("Статус заказа не PREPARING");
        }
        OrderValidationDTO validationDTO = new OrderValidationDTO(orderId, orderDTO.getOrderPositions());
        log.info("Проверка количества блюд на складе для заказа с id: {}", orderId);
        validationClient.validateOrder(validationDTO);
        log.info("Проверка количества блюд на складе для заказа с id: {} прошла успешно", orderId);
        orderDTO.setStatus(OrderStatus.SENT_TO_KITCHEN);
        kafkaSender.sendOrderCreation(orderDTO);
        orderRepository.updateOrderStatusById(orderDTO.getId(), OrderStatus.SENT_TO_KITCHEN);
        return orderDTO;
    }


    @Override
    public String getOrderStatusByOrderId(Long id) {
        log.info("Получение статуса заказа с id: {}", id);
        OrderDTO orderDTO = getOrderById(id);
        log.info("Cтатус заказа с id: {} получен, статус: {}", id, orderDTO.getStatus().name());
        return orderDTO.getStatus().name();
    }


    @Override
    public void updateOrderStatusFromKafkaDTO(UpdateOrderStatusDTO updateDTO) {
        log.info("Обновление статуса для заказа с id: {}, полученного из сервиса кухни: {}",
                updateDTO.getId(), updateDTO.getStatus());
        orderRepository.updateOrderStatusById(updateDTO.getId(), updateDTO.getStatus());
        log.info("Cтатус для заказа c id: {}, полученный из сервиса кухни, обновлен: {}",
                updateDTO.getId(), updateDTO.getStatus());
    }


    @Override
    public PaymentDTO payOrder(Long orderId, PaymentType paymentType) {
        log.info("Оплата заказа с id: {}. Тип оплаты: {}", orderId, paymentType);
        Set<OrderStatus> allowedToPayStatuses = EnumSet.of(
                OrderStatus.READY,
                OrderStatus.UNSUCCESSFUL_VISITOR_UNPAID
        );
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format(ORDER_NOT_FOUND, orderId)));

        if (!allowedToPayStatuses.contains(order.getStatus())) {
            throw new IllegalStateException(String.format("Текущий статус: %s недопустим для оплаты, "
                            + "необходимо %s или %s",
                    order.getStatus(), OrderStatus.READY, OrderStatus.UNSUCCESSFUL_VISITOR_UNPAID));
        }
        if (paymentRepository.existsById(orderId)) {
            throw new IllegalStateException(String.format("Заказ с id: '%d' уже оплачен", orderId));
        }
        Double paymentSum = calculateTotalSum(order);
        Payment payment = paymentMapper.setPaymentParameters(order, paymentType, paymentSum);
        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.updateOrderStatusById(orderId, OrderStatus.PAID_AWAITING_SERVING);
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        log.info("Заказ с id: {} успешно оплачен, статус изменен на: {}", orderId, order.getStatus());
        return paymentMapper.toPaymentDTO(savedPayment);
    }


    @Override
    public OrderDTO serveOrder(Long orderId) {
        log.info("Подача заказа с id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format(ORDER_NOT_FOUND, orderId)));
        if (OrderStatus.PAID_AWAITING_SERVING.equals(order.getStatus())
                && paymentRepository.existsById(orderId)) {
            updateAndSetStatus(order, OrderStatus.PAID_AND_SERVED);
            order.setStatus(OrderStatus.PAID_AND_SERVED);
            log.info("Заказ с id: {} подан, статус изменен на: {}", orderId, order.getStatus());
            return orderMapper.toOrderDTO(order);
        }
        throw new OrderServingException(String.format("Заказ с id: '%d' не может быть подан", orderId));
    }



    @Override
    public OrderDTO cancelOrder(Long orderId, OrderStatus newStatus) {
        log.info("Отмена заказа с id: {}. Новый статус: {}", orderId, newStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format(ORDER_NOT_FOUND, orderId)));
        OrderStatus currentStatus = order.getStatus();
        Set<OrderStatus> earlyCancelStatuses = EnumSet.of(
                OrderStatus.SENT_TO_KITCHEN,
                OrderStatus.COOKING
        );

        if (currentStatus.canTransitTo(newStatus)
                && OrderStatus.ALLOWED_CANCEL_STATUSES.contains(newStatus)) {

            if (OrderStatus.PREPARING.equals(order.getStatus())) {
                order.setStatus(OrderStatus.CANCELLED_BEFORE_SEND);
                clearOrderPositions(order.getId());
                log.info(CANCEL_SUCCESS_MESSAGE, orderId, order.getStatus());
                return orderMapper.toOrderDTO(order);
            }

            if (earlyCancelStatuses.contains(currentStatus)) {
                kafkaSender.sendUpdateOrderStatus(
                        new UpdateOrderStatusDTO(orderId, OrderStatus.CANCELLED_BY_WAITER));

                if (OrderStatus.COOKING.equals(currentStatus)) {
                    updateAndSetStatus(order, OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER);
                    log.info(CANCEL_SUCCESS_MESSAGE, orderId, order.getStatus());
                    return orderMapper.toOrderDTO(order);
                }

                updateAndSetStatus(order, OrderStatus.CANCELLED_BY_WAITER);
                log.info(CANCEL_SUCCESS_MESSAGE, orderId, order.getStatus());
                return orderMapper.toOrderDTO(order);
            }

            updateAndSetStatus(order, newStatus);
            log.info(CANCEL_SUCCESS_MESSAGE, orderId, order.getStatus());
            return orderMapper.toOrderDTO(order);
        }

        throw new IllegalStateException(String.format(
                "Статус заказа с id: '%d' не может быть переведен из статуса %s в %s",
                orderId, currentStatus, newStatus));
    }


    @Override
    public Page<OrderDTO> getOrdersByFilter(OrderFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Order> spec = OrderSpecification.withFilters(filter);
        Page<Order> resultPage = orderRepository.findAll(spec, pageable);
        return resultPage.map(orderMapper::toOrderDTO);
    }


    /**
     * Вычисляет общую стоимость всех позиций в заказе.
     *
     * @param order заказ, для которого рассчитывается сумма
     * @return общая сумма заказа
     */
    @Override
    public Double calculateTotalSum(Order order) {
        return order.getOrderPositions()
                .stream()
                .mapToDouble(orderPositionServiceImpl::getTotalCost)
                .sum();
    }


    /**
     * Добавляет позицию в заказ и устанавливает ссылку на заказ в позиции.
     *
     * @param order заказ, в который добавляется позиция
     * @param position позиция, добавляемая в заказ
     */
    @Override
    public void addPosition(Order order, OrderPosition position) {
        order.getOrderPositions().add(position);
        position.setOrder(order);
    }


    /**
     * Удаляет позицию из заказа и обнуляет ссылку на заказ в позиции.
     *
     * @param order заказ, из которого удаляется позиция
     * @param position позиция, удаляемая из заказа
     */
    @Override
    public void removePosition(Order order, OrderPosition position) {
        order.getOrderPositions().remove(position);
        position.setOrder(null);
    }


    /**
     * Внутренний метод, который обновляет статус заказа в базе данных и устанавливает его в сущности заказа.
     *
     * @param order заказ, для которого необходимо обновить статус
     * @param newStatus новый статус заказа
     */
    private void updateAndSetStatus(Order order, OrderStatus newStatus) {
        orderRepository.updateOrderStatusById(order.getId(), newStatus);
        order.setStatus(newStatus);
    }
}
