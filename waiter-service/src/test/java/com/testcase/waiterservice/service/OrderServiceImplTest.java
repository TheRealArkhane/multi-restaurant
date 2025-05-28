package com.testcase.waiterservice.service;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.commondto.waiterservice.Sex;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.dto.request.OrderCalculateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
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
import com.testcase.waiterservice.client.impl.FeignValidationClient;
import com.testcase.waiterservice.kafka.KafkaSender;
import com.testcase.waiterservice.mapper.OrderMapper;
import com.testcase.waiterservice.mapper.PaymentMapper;
import com.testcase.waiterservice.repository.MenuRepository;
import com.testcase.waiterservice.repository.order.OrderRepository;
import com.testcase.waiterservice.repository.payment.PaymentRepository;
import com.testcase.waiterservice.repository.WaiterRepository;
import com.testcase.waiterservice.service.impl.OrderPositionServiceImpl;
import com.testcase.waiterservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты для {@link OrderServiceImpl}, проверяющие корректность CRUD-операций с заказами,
 * взаимодействие с другими компонентами системы (репозитории, мапперы, Feign-клиент кухни),
 * а также бизнес-логику расчётов и статусов заказа.
 */
@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private WaiterRepository waiterRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private OrderPositionServiceImpl orderPositionServiceImpl;
    @Mock
    private FeignValidationClient feignValidationClient;
    @Mock
    private KafkaSender kafkaSender;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Order order;
    private OrderDTO orderDTO;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        waiter = new Waiter();
        waiter.setId(10L);
        waiter.setName("Иван");
        waiter.setEmploymentDate(OffsetDateTime.parse("2022-01-15T09:00:00Z"));
        waiter.setSex(Sex.MALE);

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PREPARING);
        order.setCreateDateTime(OffsetDateTime.parse("2025-04-06T21:40:36.775Z"));
        order.setTableNumber("A12");
        order.setWaiter(waiter);

        Menu menu1 = new Menu();
        menu1.setId(1L);
        menu1.setName("Пицца");
        menu1.setCost(12.5);

        Menu menu2 = new Menu();
        menu2.setId(2L);
        menu2.setName("Паста");
        menu2.setCost(10.0);

        OrderPosition position1 = new OrderPosition();
        position1.setId(100L);
        position1.setQuantity(2);
        position1.setOrder(order);
        position1.setMenu(menu1);

        OrderPosition position2 = new OrderPosition();
        position2.setId(101L);
        position2.setQuantity(1);
        position2.setOrder(order);
        position2.setMenu(menu2);

        order.setOrderPositions(new HashSet<>(Set.of(position1, position2)));

        WaiterDTO waiterDTO = new WaiterDTO();
        waiterDTO.setId(waiter.getId());
        waiterDTO.setName(waiter.getName());
        waiterDTO.setEmploymentDate(waiter.getEmploymentDate());
        waiterDTO.setSex(waiter.getSex());

        orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreateDateTime(order.getCreateDateTime());
        orderDTO.setTableNumber(order.getTableNumber());
        orderDTO.setWaiter(waiterDTO);

        MenuDTO menuDTO1 = new MenuDTO();
        menuDTO1.setId(menu1.getId());
        menuDTO1.setName(menu1.getName());
        menuDTO1.setCost(menu1.getCost());

        MenuDTO menuDTO2 = new MenuDTO();
        menuDTO2.setId(menu2.getId());
        menuDTO2.setName(menu2.getName());
        menuDTO2.setCost(menu2.getCost());

        OrderPositionDTO positionDTO1 = new OrderPositionDTO();
        positionDTO1.setOrderId(order.getId());
        positionDTO1.setQuantity(position1.getQuantity());
        positionDTO1.setMenu(menuDTO1);

        OrderPositionDTO positionDTO2 = new OrderPositionDTO();
        positionDTO2.setOrderId(order.getId());
        positionDTO2.setQuantity(position2.getQuantity());
        positionDTO2.setMenu(menuDTO2);

        orderDTO.setOrderPositions(Set.of(positionDTO1, positionDTO2));
    }


    /**
     * Проверяет получение списка всех заказов.
     * given: Настроенные моки репозитория (возвращающие список) и маппера.
     * when: Вызывается метод getAllOrders.
     * then: Возвращается список DTO, проверяется взаимодействие с репозиторием.
     */
    @Test
    @DisplayName("Успешное получение всех заказов")
    void getAllOrders_Success() {
        // given
        List<Order> orders = List.of(order);
        List<OrderDTO> ordersDTO = List.of(orderDTO);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toOrderDTOList(orders)).thenReturn(ordersDTO);

        // when
        List<OrderDTO> result = orderServiceImpl.getAllOrders();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO.getId(), result.getFirst().getId());
        verify(orderRepository).findAll();
    }


    /**
     * Проверяет получение заказа по существующему ID.
     * given: Существующий ID заказа и настроенные моки репозитория и маппера.
     * when: Вызывается метод getOrderById с валидным ID.
     * then: Возвращается корректный DTO, проверяется взаимодействие с зависимостями.
     */
    @Test
    @DisplayName("Успешное получение заказа по ID")
    void getOrderById_Success() {
        // given
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        OrderDTO result = orderServiceImpl.getOrderById(order.getId());

        // then
        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
        assertEquals(orderDTO.getTableNumber(), result.getTableNumber());
        verify(orderRepository).findById(order.getId());
    }


    /**
     * Проверяет обработку запроса несуществующего заказа.
     * given: Несуществующий ID заказа.
     * when: Вызывается метод getOrderById с невалидным ID.
     * then: Выбрасывается OrderNotFoundException.
     */
    @Test
    @DisplayName("Получение несуществующего заказа")
    void getOrderById_OrderNotFound() {
        // given
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        // when
        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class,
                () -> orderServiceImpl.getOrderById(order.getId()));

        // then
        assertEquals(String.format("Заказ с id: '%d' не найден", order.getId()), ex.getMessage());
        verify(orderRepository).findById(order.getId());
    }


    /**
     * Проверяет создание нового заказа с валидными параметрами.
     * given: Корректные данные запроса и существующий официант.
     * when: Вызывается метод createOrder.
     * then: Заказ сохраняется, возвращается DTO с правильными параметрами.
     */
    @Test
    @DisplayName("Создание заказа - успешно")
    void createOrder_Success() {
        // given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO();
        requestDTO.setWaiterId(waiter.getId());
        requestDTO.setTableNumber("A12");


        Order newOrder = new Order();
        newOrder.setTableNumber(requestDTO.getTableNumber());
        newOrder.setWaiter(waiter);
        newOrder.setStatus(OrderStatus.PREPARING);
        newOrder.setCreateDateTime(OffsetDateTime.now());

        Order savedOrder = new Order();
        savedOrder.setId(2L);
        savedOrder.setTableNumber(newOrder.getTableNumber());
        savedOrder.setWaiter(newOrder.getWaiter());
        savedOrder.setStatus(newOrder.getStatus());
        savedOrder.setCreateDateTime(newOrder.getCreateDateTime());

        OrderDTO savedOrderDTO = new OrderDTO();
        savedOrderDTO.setId(savedOrder.getId());
        savedOrderDTO.setStatus(savedOrder.getStatus());
        savedOrderDTO.setCreateDateTime(savedOrder.getCreateDateTime());
        savedOrderDTO.setTableNumber(savedOrder.getTableNumber());

        when(waiterRepository.findById(waiter.getId())).thenReturn(Optional.of(waiter));
        when(orderMapper.setOrderWhileCreating(requestDTO, waiter)).thenReturn(newOrder);
        when(orderRepository.save(newOrder)).thenReturn(savedOrder);
        when(orderMapper.toOrderDTO(savedOrder)).thenReturn(savedOrderDTO);

        // when
        OrderDTO result = orderServiceImpl.createOrder(requestDTO);

        // then
        assertNotNull(result);
        assertEquals(savedOrderDTO.getId(), result.getId());
        assertEquals(savedOrderDTO.getTableNumber(), result.getTableNumber());
        assertEquals(savedOrderDTO.getWaiter(), result.getWaiter());
        assertEquals(savedOrderDTO.getStatus(), result.getStatus());
        assertEquals(savedOrderDTO.getCreateDateTime(), result.getCreateDateTime());
        verify(waiterRepository).findById(waiter.getId());
        verify(orderRepository).save(newOrder);
    }


    /**
     * Проверяет обработку отсутствия официанта при создании заказа.
     * given: Несуществующий ID официанта.
     * when: Вызывается метод createOrder с невалидным waiterId.
     * then: Выбрасывается WaiterNotFoundException.
     */
    @Test
    @DisplayName("Создание заказа с несуществующим официантом")
    void createOrder_WaiterNotFound() {
        // given
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO();
        requestDTO.setWaiterId(waiter.getId());
        requestDTO.setTableNumber("A12");

        when(waiterRepository.findById(waiter.getId())).thenReturn(Optional.empty());

        // when
        WaiterNotFoundException ex = assertThrows(WaiterNotFoundException.class,
                () -> orderServiceImpl.createOrder(requestDTO));

        // then
        assertEquals(String.format("Официант с id: %d не найден", waiter.getId()), ex.getMessage());
        verify(waiterRepository).findById(waiter.getId());
    }


    /**
     * Проверяет добавление новой позиции в заказ.
     * given: Существующий заказ и новая позиция меню.
     * when: Вызывается метод calculateOrder с новым menuId.
     * then: Позиция добавляется, проверяется сохранение изменений.
     */
    @Test
    @DisplayName("Добавление новой позиции в заказ")
    void calculateOrder_NewPosition_Success() {
        // given
        OrderCalculateRequestDTO requestDTO = new OrderCalculateRequestDTO();
        requestDTO.setMenuId(999L);
        requestDTO.setQuantity(3);

        Menu newDish = new Menu();
        newDish.setId(999L);
        newDish.setName("Салат");
        newDish.setCost(8.0);

        OrderPosition newPosition = new OrderPosition();
        newPosition.setId(102L);
        newPosition.setQuantity(3);
        newPosition.setMenu(newDish);
        newPosition.setOrder(order);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(menuRepository.findById(999L)).thenReturn(Optional.of(newDish));
        when(orderPositionServiceImpl.createOrderPosition(newDish.getId(), requestDTO.getQuantity(), order.getId()))
                .thenReturn(newPosition);
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        OrderDTO result = orderServiceImpl.calculateOrder(order.getId(), requestDTO);

        // then
        assertTrue(order.getOrderPositions().contains(newPosition));
        assertEquals(3, order.getOrderPositions().size());
        verify(orderPositionServiceImpl).createOrderPosition(newDish.getId(), requestDTO.getQuantity(), order.getId());
        verify(orderRepository).save(order);
        assertNotNull(result);
    }


    /**
     * Проверяет обновление количества существующей позиции.
     * given: Заказ с существующей позицией и новое количество.
     * when: Вызывается метод calculateOrder с существующим menuId.
     * then: Количество обновляется, проверяется вызов сервиса позиций.
     */
    @Test
    @DisplayName("Обновление существующей позиции в заказе")
    void calculateOrder_ExistingPosition_Success() {
        // given
        OrderCalculateRequestDTO requestDTO = new OrderCalculateRequestDTO();
        requestDTO.setMenuId(1L);
        requestDTO.setQuantity(3);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Menu dish = new Menu();
        dish.setId(1L);
        dish.setName("Пицца");
        dish.setCost(12.5);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        OrderDTO result = orderServiceImpl.calculateOrder(order.getId(), requestDTO);

        // then
        verify(orderPositionServiceImpl).updateOrderPosition(100L, 2 + 3);
        verify(orderRepository).save(order);
        assertNotNull(result);
    }


    /**
     * Проверяет удаление позиции при нулевом количестве.
     * Для блюда с id 1 уже существует позиция (quantity = 2), добавляем отрицательное количество,
     * чтобы сумма стала <= 0, и позиция удалится
     * given: Позиция с quantity = 0 после обновления.
     * when: Вызывается метод calculateOrder с отрицательным количеством.
     * then: Позиция удаляется, проверяется обновление заказа.
     */
    @Test
    @DisplayName("Удаление позиции при нулевом количестве")
    void calculateOrder_Position_Deleting_Success() {
        // given
        OrderCalculateRequestDTO requestDTO = new OrderCalculateRequestDTO();
        requestDTO.setMenuId(1L);
        requestDTO.setQuantity(-2);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Пицца");
        menu.setCost(12.5);
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        orderServiceImpl.calculateOrder(order.getId(), requestDTO);

        // then
        verify(orderRepository).save(order);
        // Проверяем, что позиция с id = 100L более не содержится в заказе
        assertFalse(order.getOrderPositions().stream().anyMatch(pos -> pos.getId().equals(100L)));
    }


    /**
     * Проверяет попытку подсчета заказа в недопустимом статусе.
     * given: Заказ с статусом отличным от PREPARING.
     * when: Вызывается метод calculateOrder.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Попытка подсчета заказа в недопустимом статусе")
    void calculateOrder_WrongStatus() {
        // given
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        OrderCalculateRequestDTO requestDTO = new OrderCalculateRequestDTO();
        requestDTO.setMenuId(1L);
        requestDTO.setQuantity(3);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.calculateOrder(order.getId(), requestDTO));

        // then
        assertEquals(ex.getMessage(),
                String.format("Невозможно произвести подсчет заказа со статусом: %s, требуется %s",
                order.getStatus(), OrderStatus.PREPARING));
    }


    /**
     * Проверяет обработку добавления несуществующей позиции меню.
     * given: Несуществующий ID позиции меню.
     * when: Вызывается метод calculateOrder с невалидным menuId.
     * then: Выбрасывается MenuPositionNotFoundException.
     */
    @Test
    @DisplayName("Добавление несуществующей позиции меню")
    void calculateOrder_MenuNotFound() {
        // given
        OrderCalculateRequestDTO requestDTO = new OrderCalculateRequestDTO();
        requestDTO.setMenuId(999L);
        requestDTO.setQuantity(3);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Exception ex = assertThrows(MenuPositionNotFoundException.class,
                () -> orderServiceImpl.calculateOrder(order.getId(), requestDTO));

        // then
        assertEquals(ex.getMessage(), String.format("Позиция меню с id: '%d' не найдена", requestDTO.getMenuId()));
    }


    /**
     * Проверяет очистку позиций заказа в допустимом статусе (PREPARING).
     * given: Заказ со статусом PREPARING и позициями.
     * when: Вызывается метод clearOrderPositions.
     * then: Позиции очищаются, проверяется сохранение.
     */
    @Test
    @DisplayName("Очистка позиций заказа в статусе PREPARING")
    void clearOrderPositions_AllowedStatuses_Preparing_Success() {
        // given
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // when
        orderServiceImpl.clearOrderPositions(order.getId());

        // then
        assertTrue(order.getOrderPositions().isEmpty());
        verify(orderRepository).save(order);
    }


    /**
     * Проверяет очистку позиций заказа в допустимом статусе (CANCELLED_BEFORE_SEND).
     * given: Заказ со статусом CANCELLED_BEFORE_SEND и позициями.
     * when: Вызывается метод clearOrderPositions.
     * then: Позиции очищаются, проверяется сохранение.
     */
    @Test
    @DisplayName("Очистка позиций заказа в статусе CANCELLED_BEFORE_SEND")
    void clearOrderPositions_AllowedStatuses_CancelledBeforeSend_Success() {
        // given
        order.setStatus(OrderStatus.CANCELLED_BEFORE_SEND);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // when
        orderServiceImpl.clearOrderPositions(order.getId());

        // then
        assertTrue(order.getOrderPositions().isEmpty());
        verify(orderRepository).save(order);
    }


    /**
     * Проверяет попытку очистки позиций в недопустимом статусе.
     * given: Заказ с неподходящим статусом.
     * when: Вызывается метод clearOrderPositions.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Очистка позиций заказа в недопустимом статусе")
    void clearOrderPositions_WrongStatus() {
        // given
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.clearOrderPositions(order.getId()));

        // then
        assertEquals(ex.getMessage(), String.format("Невозможно удалить позиции заказа со статусом: %s, требуется %s или %s",
                order.getStatus(), OrderStatus.PREPARING, OrderStatus.CANCELLED_BEFORE_SEND));
    }


    /**
     * Проверяет успешную отправку заказа на кухню.
     * given: Валидный заказ с позициями и статусом PREPARING.
     * when: Вызывается метод sendOrderToKitchen.
     * then: Статус обновляется на SENT_TO_KITCHEN, проверяется вызов Feign-клиента.
     */
    @Test
    @DisplayName("Успешная отправка заказа на кухню")
    void sendOrderToKitchen_Success() {
        // given
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);
        when(feignValidationClient.validateOrder(any(OrderValidationDTO.class))).thenReturn(true);

        // when
        OrderDTO result = orderServiceImpl.sendOrderToKitchen(order.getId());

        // then
        verify(feignValidationClient).validateOrder(any(OrderValidationDTO.class));
        verify(orderRepository).updateOrderStatusById(orderDTO.getId(), OrderStatus.SENT_TO_KITCHEN);
        assertEquals(OrderStatus.SENT_TO_KITCHEN, result.getStatus());
    }


    /**
     * Проверяет отправку пустого заказа.
     * given: Заказ без позиций.
     * when: Вызывается метод sendOrderToKitchen.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Отправка пустого заказа на кухню")
    void sendOrderToKitchen_EmptyPositions() {
        // given
        OrderDTO emptyOrderDTO = new OrderDTO();
        emptyOrderDTO.setId(order.getId());
        emptyOrderDTO.setStatus(OrderStatus.PREPARING);
        emptyOrderDTO.setOrderPositions(Set.of());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(emptyOrderDTO);

        // when
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.sendOrderToKitchen(order.getId()));

        // then
        assertEquals(ex.getMessage(), String.format("Заказ с id: '%d' не должен быть пустым", order.getId()));
    }


    /**
     * Проверяет отправку заказа в недопустимом статусе.
     * given: Заказ с неподходящим статусом.
     * when: Вызывается метод sendOrderToKitchen.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Отправка заказа в недопустимом статусе")
    void sendOrderToKitchen_WrongStatus() {
        // given
        OrderDTO wrongStatusDTO = new OrderDTO();
        wrongStatusDTO.setId(order.getId());
        wrongStatusDTO.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        wrongStatusDTO.setOrderPositions(orderDTO.getOrderPositions());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(wrongStatusDTO);

        // when
        Exception ex = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.sendOrderToKitchen(order.getId()));

        // then
        assertTrue(ex.getMessage().contains("Статус заказа не PREPARING"));
    }


    /**
     * Проверяет обработку ошибки валидации заказа кухней.
     * given: Невалидный заказ (по мнению кухни).
     * when: Вызывается метод sendOrderToKitchen.
     * then: Выбрасывается RuntimeException.
     */
    @Test
    @DisplayName("Ошибка валидации заказа кухней")
    void sendOrderToKitchen_ValidationFailing() {
        // given
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);
        doThrow(new RuntimeException("Валидация неуспешна"))
                .when(feignValidationClient).validateOrder(any(OrderValidationDTO.class));

        // when
        Exception ex = assertThrows(RuntimeException.class,
                () -> orderServiceImpl.sendOrderToKitchen(order.getId()));

        // then
        assertTrue(ex.getMessage().contains("Валидация неуспешна"));
    }


    /**
     * Проверяет получение текущего статуса заказа.
     * given: Существующий заказ.
     * when: Вызывается метод getOrderStatusByOrderId.
     * then: Возвращается корректный статус.
     */
    @Test
    @DisplayName("Получение статуса заказа по ID")
    void getOrderStatusById_Success() {
        // given
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        String status = orderServiceImpl.getOrderStatusByOrderId(order.getId());

        // then
        assertEquals(orderDTO.getStatus().name(), status);
    }


    /**
     * Проверяет обработку DTO обновления статуса из Kafka.
     * given: DTO с новым статусом заказа.
     * when: Вызывается метод updateOrderStatusFromKafkaDTO.
     * then: Статус заказа обновляется в репозитории.
     */
    @Test
    @DisplayName("Обновление статуса заказа из Kafka")
    void updateOrderStatusFromKafkaDTO_Success() {
        // given
        UpdateOrderStatusDTO updateDTO = new UpdateOrderStatusDTO(1L, OrderStatus.PAID_AWAITING_SERVING);

        // when
        orderServiceImpl.updateOrderStatusFromKafkaDTO(updateDTO);

        // then
        verify(orderRepository).updateOrderStatusById(1L, OrderStatus.PAID_AWAITING_SERVING);
    }


    /**
     * Проверяет успешную оплату заказа.
     * given: Неоплаченный заказ и валидный тип оплаты.
     * when: Вызывается метод payOrder.
     * then: Создается платеж, статус обновляется на PAID_AWAITING_SERVING.
     */
    @Test
    @DisplayName("Успешная оплата заказа")
    void payOrder_Success() {
        // given
        Payment payment = new Payment();
        PaymentDTO paymentDTO = new PaymentDTO();

        order.setStatus(OrderStatus.READY);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsById(order.getId())).thenReturn(false);
        when(paymentMapper.setPaymentParameters(
                order,
                PaymentType.CARD,
                orderServiceImpl.calculateTotalSum(order)))
                .thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentDTO(payment)).thenReturn(paymentDTO);

        // when
        orderServiceImpl.payOrder(order.getId(), PaymentType.CARD);

        // then
        verify(orderRepository).updateOrderStatusById(order.getId(), OrderStatus.PAID_AWAITING_SERVING);
        verify(paymentRepository).save(payment);
    }


    /**
     * Проверяет попытку повторной оплаты заказа.
     * given: Уже оплаченный заказ.
     * when: Вызывается метод payOrder.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Попытка повторной оплаты заказа")
    void payOrder_PaymentExists() {
        // given
        order.setStatus(OrderStatus.READY);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsById(order.getId())).thenReturn(true);

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderServiceImpl.payOrder(order.getId(), PaymentType.CARD));

        // then
        assertEquals(String.format("Заказ с id: '%d' уже оплачен", order.getId()), ex.getMessage());
    }


    /**
     * Проверяет успешную подачу оплаченного заказа.
     * given: Заказ со статусом PAID_AWAITING_SERVING.
     * when: Вызывается метод serveOrder.
     * then: Статус обновляется на PAID_AND_SERVED.
     */
    @Test
    @DisplayName("Успешная подача заказа")
    void serveOrder_Success() {
        // given
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsById(order.getId())).thenReturn(true);
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        orderServiceImpl.serveOrder(order.getId());

        // then
        assertEquals(OrderStatus.PAID_AND_SERVED, order.getStatus());
        verify(orderRepository).updateOrderStatusById(order.getId(), OrderStatus.PAID_AND_SERVED);
    }


    /**
     * Проверяет попытку подачи заказа в недопустимом статусе.
     * given: Заказ с неподходящим статусом.
     * when: Вызывается метод serveOrder.
     * then: Выбрасывается OrderServingException.
     */
    @Test
    @DisplayName("Попытка подачи заказа в недопустимом статусе")
    void serveOrder_WrongStatus() {
        // given
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when
        OrderServingException ex = assertThrows(OrderServingException.class,
                () -> orderServiceImpl.serveOrder(order.getId()));

        // then
        assertEquals(String.format("Заказ с id: '%d' не может быть подан", order.getId()), ex.getMessage());
    }


    /**
     * Проверяет попытку подачи неоплаченного заказа.
     * given: Заказ без подтвержденной оплаты.
     * when: Вызывается метод serveOrder.
     * then: Выбрасывается OrderServingException.
     */
    @Test
    @DisplayName("Попытка подачи неоплаченного заказа")
    void serveOrder_EmptyPayment() {
        // given
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.existsById(order.getId())).thenReturn(false);

        // when
        OrderServingException ex = assertThrows(OrderServingException.class,
                () -> orderServiceImpl.serveOrder(order.getId()));

        // then
        assertEquals(String.format("Заказ с id: '%d' не может быть подан", order.getId()), ex.getMessage());
    }


    /**
     * Проверяет успешную отмену заказа с допустимым переходом статуса.
     * given: Заказ в статусе PAID_AWAITING_SERVING.
     * when: Вызывается метод cancelOrder с новым статусом.
     * then: Статус заказа обновляется.
     */
    @Test
    @DisplayName("Успешная отмена заказа")
    void cancelOrder_Success() {
        // given
        order.setStatus(OrderStatus.PAID_AWAITING_SERVING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        orderServiceImpl.cancelOrder(order.getId(), OrderStatus.UNSUCCESSFUL_VISITOR_REASON);

        // then
        assertEquals(OrderStatus.UNSUCCESSFUL_VISITOR_REASON, order.getStatus());
        verify(orderMapper).toOrderDTO(order);
    }


    /**
     * Проверяет попытку отмены с недопустимым целевым статусом.
     * given: Неправильный целевой статус для отмены.
     * when: Вызывается метод cancelOrder.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    void cancelOrder_StatusNotAllowed() {
        // given
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when + then
        assertThrows(IllegalStateException.class, () ->
                orderServiceImpl.cancelOrder(order.getId(), OrderStatus.PAID_AWAITING_SERVING)
        );
    }


    /**
     * Проверяет попытку невозможного перехода статуса при отмене.
     * given: Текущий статус не позволяет переход к целевому.
     * when: Вызывается метод cancelOrder.
     * then: Выбрасывается IllegalStateException.
     */
    @Test
    @DisplayName("Невозможный переход статуса при отмене")
    void cancelOrder_StatusCantTransit() {
        // given
        order.setStatus(OrderStatus.PAID_AND_SERVED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when + then
        assertThrows(IllegalStateException.class, () ->
                orderServiceImpl.cancelOrder(order.getId(), OrderStatus.CANCELLED_BY_WAITER)
        );
    }


    /**
     * Проверяет отмену заказа на стадии подготовки.
     * given: Заказ в статусе PREPARING.
     * when: Вызывается метод cancelOrder.
     * then: Статус меняется на CANCELLED_BEFORE_SEND.
     */
    @Test
    @DisplayName("Отмена заказа на стадии подготовки")
    void cancelOrder_EarlyStatuses_Preparing() {
        // given
        order.setStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        orderDTO.setStatus(OrderStatus.CANCELLED_BEFORE_SEND);
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        OrderDTO result = orderServiceImpl.cancelOrder(order.getId(), OrderStatus.CANCELLED_BEFORE_SEND);

        //then
        assertEquals(OrderStatus.CANCELLED_BEFORE_SEND, result.getStatus());
        verify(orderRepository).save(order);
    }


    /**
     * Проверяет отмену заказа во время готовки.
     * given: Заказ в статусе COOKING.
     * when: Вызывается метод cancelOrder.
     * then: Статус меняется на CANCELLED_WHILE_COOKING_BY_WAITER.
     */
    @Test
    @DisplayName("Отмена заказа во время готовки")
    void cancelOrder_EarlyStatuses_Cooking() {
        // given
        order.setStatus(OrderStatus.COOKING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDTO(order)).thenReturn(orderDTO);

        // when
        orderServiceImpl.cancelOrder(order.getId(), OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER);

        // then
        assertEquals(OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER, order.getStatus());
        verify(orderMapper).toOrderDTO(order);
    }
}