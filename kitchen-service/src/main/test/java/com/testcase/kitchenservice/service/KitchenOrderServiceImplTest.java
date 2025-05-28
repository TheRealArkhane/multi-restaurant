package java.com.testcase.kitchenservice.service;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.dto.DishBalanceUpdateDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.dto.OrderToDishDTO;
import com.testcase.kitchenservice.entity.Dish;
import com.testcase.kitchenservice.entity.KitchenOrder;
import com.testcase.kitchenservice.entity.OrderToDish;
import com.testcase.kitchenservice.exception.InvalidOrderStatusException;
import com.testcase.kitchenservice.exception.KitchenOrderNotFoundException;
import com.testcase.kitchenservice.kafka.KafkaSender;
import com.testcase.kitchenservice.mapper.KitchenOrderMapper;
import com.testcase.kitchenservice.mapstruct.mapper.KitchenOrderMapstructMapper;
import com.testcase.kitchenservice.service.impl.DishServiceImpl;
import com.testcase.kitchenservice.service.impl.KitchenOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для {@link KitchenOrderServiceImpl}. Содержит тесты для различных операций,
 * связанных с обработкой заказов на кухне, включая обновление статуса заказов, создание заказов и валидацию.
 */
@ExtendWith(MockitoExtension.class)
public class KitchenOrderServiceImplTest {

    @Mock
    private DishServiceImpl dishServiceImpl;
    @Mock
    private KitchenOrderMapper kitchenOrderMapper;
    @Mock
    private KitchenOrderMapstructMapper kitchenOrderMapstructMapper;
    @Mock
    private KafkaSender kafkaSender;

    @InjectMocks
    private KitchenOrderServiceImpl kitchenOrderServiceImpl;

    private KitchenOrder kitchenOrder;
    private KitchenOrderDTO kitchenOrderDTO;
    private UpdateOrderStatusDTO updateStatusDTO;

    @BeforeEach
    void setUp() {
        kitchenOrder = new KitchenOrder();
        kitchenOrder.setKitchenOrderId(1L);
        kitchenOrder.setWaiterOrderId(10L);
        kitchenOrder.setStatus(OrderStatus.SENT_TO_KITCHEN);
        kitchenOrder.setCreateDateTime(OffsetDateTime.parse("2025-04-06T21:40:36.775Z"));
        kitchenOrder.setOrderToDishes(new HashSet<>());

        OrderToDish orderToDish = new OrderToDish();
        orderToDish.setDishId(1L);
        orderToDish.setDishesCount(2);
        kitchenOrder.getOrderToDishes().add(orderToDish);

        kitchenOrderDTO = new KitchenOrderDTO();
        kitchenOrderDTO.setKitchenOrderId(kitchenOrder.getKitchenOrderId());
        kitchenOrderDTO.setWaiterOrderId(kitchenOrder.getWaiterOrderId());
        kitchenOrderDTO.setStatus(kitchenOrder.getStatus());
        kitchenOrderDTO.setCreateDateTime(kitchenOrder.getCreateDateTime());

        OrderToDishDTO orderToDishDTO = new OrderToDishDTO();
        orderToDishDTO.setDishId(1L);
        orderToDishDTO.setDishesCount(2);
        kitchenOrderDTO.setOrderToDishes(new HashSet<>(Set.of(orderToDishDTO)));
    }


    /**
     * Проверяет успешное обновление статуса заказа из SENT_TO_KITCHEN в CANCELLED_BY_KITCHEN.
     * given: Заказ в статусе SENT_TO_KITCHEN и валидные данные блюд.
     * when: Вызывается метод updateOrderStatus с новым статусом.
     * then: Статус обновляется, баланс блюд восстанавливается, проверяются вызовы зависимостей.
     */
    @Test
    @DisplayName("Обновление статуса заказа из SENT_TO_KITCHEN")
    void updateOrderStatus_SentToKitchen_Success() {
        // given
        kitchenOrder.setStatus(OrderStatus.SENT_TO_KITCHEN);
        OrderStatus newStatus = OrderStatus.CANCELLED_BY_KITCHEN;

        when(kitchenOrderMapper.getKitchenOrderById(1L)).thenReturn(Optional.of(kitchenOrder));
        when(kitchenOrderMapstructMapper.toKitchenOrderDTO(kitchenOrder)).thenReturn(kitchenOrderDTO);

        Set<DishBalanceUpdateDTO> expectedUpdates = kitchenOrder.getOrderToDishes().stream()
                .map(orderToDish -> new DishBalanceUpdateDTO(orderToDish.getDishId(), orderToDish.getDishesCount()))
                .collect(Collectors.toSet());

        ArgumentCaptor<Set<DishBalanceUpdateDTO>> captor = ArgumentCaptor.forClass(Set.class);

        // when
        KitchenOrderDTO result = kitchenOrderServiceImpl.updateOrderStatus(1L, newStatus);

        // then
        verify(dishServiceImpl).batchUpdateDishBalances(captor.capture());
        Set<DishBalanceUpdateDTO> actualUpdates = captor.getValue();
        assertEquals(expectedUpdates, actualUpdates);

        verify(kitchenOrderMapper).updateStatus(kitchenOrder);
        assertEquals(newStatus, kitchenOrder.getStatus());
        assertEquals(kitchenOrderDTO, result);
    }

    /**
     * Проверяет успешное обновление статуса заказа из COOKING в CANCELLED_WHILE_COOKING_BY_KITCHEN.
     * given: Заказ в статусе COOKING.
     * when: Вызывается метод updateOrderStatus с новым статусом.
     * then: Статус изменяется, проверяется сохранение изменений.
     */
    @Test
    @DisplayName("Обновление статуса заказа из COOKING")
    void updateOrderStatus_Cooking_Success() {
        // given
        kitchenOrder.setStatus(OrderStatus.COOKING);
        OrderStatus newStatus = OrderStatus.CANCELLED_BY_KITCHEN;

        when(kitchenOrderMapper.getKitchenOrderById(1L)).thenReturn(Optional.of(kitchenOrder));
        when(kitchenOrderMapstructMapper.toKitchenOrderDTO(kitchenOrder)).thenReturn(kitchenOrderDTO);

        // when
        KitchenOrderDTO result = kitchenOrderServiceImpl.updateOrderStatus(1L, newStatus);

        // then
        assertEquals(OrderStatus.CANCELLED_WHILE_COOKING_BY_KITCHEN, kitchenOrder.getStatus());
        verify(kitchenOrderMapper).updateStatus(kitchenOrder);
        assertEquals(kitchenOrderDTO, result);
    }


    /**
     * Проверяет обработку отмены заказа официантом через Kafka.
     * given: Заказ в статусе SENT_TO_KITCHEN и DTO обновления статуса.
     * when: Вызывается метод updateOrderStatusFromWaiterServiceByKafka.
     * then: Статус обновляется, баланс блюд восстанавливается.
     */
    @Test
    @DisplayName("Обработка отмены заказа из Kafka (SENT_TO_KITCHEN)")
    void updateOrderStatusFromWaiterServiceByKafka_SentToKitchen_Success() {
        // given
        updateStatusDTO = new UpdateOrderStatusDTO(1L, OrderStatus.CANCELLED_BY_WAITER);
        kitchenOrder.setStatus(OrderStatus.SENT_TO_KITCHEN);

        when(kitchenOrderMapper.getKitchenOrderById(1L)).thenReturn(Optional.of(kitchenOrder));

        Set<DishBalanceUpdateDTO> expectedUpdates = kitchenOrder.getOrderToDishes().stream()
                .map(orderToDish -> new DishBalanceUpdateDTO(orderToDish.getDishId(), orderToDish.getDishesCount()))
                .collect(Collectors.toSet());

        ArgumentCaptor<Set<DishBalanceUpdateDTO>> captor = ArgumentCaptor.forClass(Set.class);

        // when
        kitchenOrderServiceImpl.updateOrderStatusFromWaiterServiceByKafka(updateStatusDTO);

        // then
        verify(dishServiceImpl).batchUpdateDishBalances(captor.capture());
        assertEquals(expectedUpdates, captor.getValue());

        assertEquals(OrderStatus.CANCELLED_BY_WAITER, kitchenOrder.getStatus());
        verify(kitchenOrderMapper).updateStatus(kitchenOrder);
    }


    /**
     * Проверяет обработку отмены заказа во время готовки через Kafka.
     * given: Заказ в статусе COOKING и DTO обновления статуса.
     * when: Вызывается метод updateOrderStatusFromWaiterServiceByKafka.
     * then: Статус меняется на CANCELLED_WHILE_COOKING_BY_WAITER.
     */
    @Test
    @DisplayName("Обработка отмены заказа из Kafka (COOKING)")
    void updateOrderStatusFromWaiterServiceByKafka_Cooking_Success() {
        // given
        updateStatusDTO = new UpdateOrderStatusDTO(1L, OrderStatus.CANCELLED_BY_WAITER);
        kitchenOrder.setStatus(OrderStatus.COOKING);

        when(kitchenOrderMapper.getKitchenOrderById(1L)).thenReturn(Optional.of(kitchenOrder));

        // when
        kitchenOrderServiceImpl.updateOrderStatusFromWaiterServiceByKafka(updateStatusDTO);

        // then
        assertEquals(OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER, kitchenOrder.getStatus());
        verify(kitchenOrderMapper).updateStatus(kitchenOrder);
    }


    /**
     * Проверяет обработку запроса несуществующего заказа.
     * given: Несуществующий ID заказа.
     * when: Вызывается метод updateOrderStatus.
     * then: Выбрасывается KitchenOrderNotFoundException.
     */
    @Test
    @DisplayName("Обновление статуса несуществующего заказа")
    void updateOrderStatus_KitchenOrderNotFound() {
        // given
        Long nonExistingId = 999L;
        when(kitchenOrderMapper.getKitchenOrderById(nonExistingId)).thenReturn(Optional.empty());

        // when
        KitchenOrderNotFoundException ex = assertThrows(KitchenOrderNotFoundException.class, () ->
                kitchenOrderServiceImpl.updateOrderStatus(nonExistingId, OrderStatus.COOKING)
        );

        // then
        assertEquals(ex.getMessage(), String.format("Заказ с id: '%d' не найден", nonExistingId));
    }


    /**
     * Проверяет попытку недопустимого перехода статуса.
     * given: Заказ в статусе READY и целевой статус CANCELLED_BY_KITCHEN.
     * when: Вызывается метод updateOrderStatus.
     * then: Выбрасывается InvalidOrderStatusException.
     */
    @Test
    @DisplayName("Недопустимый переход статуса заказа")
    void updateOrderStatus_CantTransit() {
        // given
        kitchenOrder.setStatus(OrderStatus.READY);
        OrderStatus newStatus = OrderStatus.CANCELLED_BY_KITCHEN;

        when(kitchenOrderMapper.getKitchenOrderById(1L)).thenReturn(Optional.of(kitchenOrder));

        // when
        InvalidOrderStatusException ex = assertThrows(InvalidOrderStatusException.class, () ->
                kitchenOrderServiceImpl.updateOrderStatus(1L, newStatus)
        );

        // then
        assertEquals(ex.getMessage(), String.format("Переход из статуса '%s' в '%s' невозможен", kitchenOrder.getStatus(), newStatus));
    }


    /**
     * Проверяет фильтрацию заказов по критериям.
     * given: Критерии поиска (статус, временной диапазон, ID официанта).
     * when: Вызывается метод getKitchenOrdersByCriteria.
     * then: Возвращается отфильтрованный список заказов.
     */
    @Test
    @DisplayName("Поиск заказов по критериям")
    void getKitchenOrdersByCriteria_AllCriteries_Success() {
        // given
        CriteriaDTO criteria = new CriteriaDTO();
        criteria.setStatus(OrderStatus.SENT_TO_KITCHEN);
        criteria.setCreatedFrom(OffsetDateTime.parse("2025-04-01T00:00:00Z"));
        criteria.setCreatedTo(OffsetDateTime.parse("2025-04-30T23:59:59Z"));
        criteria.setWaiterId(10L);

        List<KitchenOrder> orders = List.of(kitchenOrder);
        List<KitchenOrderDTO> ordersDTO = List.of(kitchenOrderDTO);

        when(kitchenOrderMapper.getKitchenOrdersByCriteria(criteria)).thenReturn(orders);
        when(kitchenOrderMapstructMapper.toKitchenOrderDTOList(orders)).thenReturn(ordersDTO);

        // when
        List<KitchenOrderDTO> result = kitchenOrderServiceImpl.getKitchenOrdersByCriteria(criteria);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(kitchenOrderMapper).getKitchenOrdersByCriteria(criteria);
        verify(kitchenOrderMapstructMapper).toKitchenOrderDTOList(orders);
    }


    /**
     * Проверяет получение заказа по ID.
     * given: Существующий ID заказа.
     * when: Вызывается метод getKitchenOrderById.
     * then: Возвращается корректный DTO заказа.
     */
    @Test
    @DisplayName("Получение заказа по ID")
    void getKitchenOrderById_Success() {
        // given
        Long kitchenOrderId = kitchenOrder.getKitchenOrderId();
        when(kitchenOrderMapper.getKitchenOrderById(kitchenOrderId)).thenReturn(Optional.of(kitchenOrder));
        when(kitchenOrderMapstructMapper.toKitchenOrderDTO(kitchenOrder)).thenReturn(kitchenOrderDTO);

        // when
        KitchenOrderDTO result = kitchenOrderServiceImpl.getKitchenOrderById(kitchenOrderId);

        // then
        assertNotNull(result);
        assertEquals(kitchenOrderId, result.getKitchenOrderId());
        verify(kitchenOrderMapper).getKitchenOrderById(kitchenOrderId);
        verify(kitchenOrderMapstructMapper).toKitchenOrderDTO(kitchenOrder);
    }


    /**
     * Проверяет создание заказа на кухне из DTO.
     * given: Валидный OrderDTO с позициями.
     * when: Вызывается метод createKitchenOrderFromOrderDTO.
     * then: Заказ сохраняется, проверяются вызовы зависимостей.
     */
    @Test
    @DisplayName("Создание заказа из DTO")
    void createKitchenOrderFromOrderDTO_Success() {
        // given

        OrderToDish orderToDish = new OrderToDish();
        orderToDish.setDishId(1L);
        orderToDish.setDishesCount(2);

        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(1L);
        menuDTO.setName("Pizza");
        menuDTO.setCost(100.0);

        OrderPositionDTO orderPositionDTO = new OrderPositionDTO();
        orderPositionDTO.setMenu(menuDTO);
        orderPositionDTO.setQuantity(2);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setStatus(OrderStatus.SENT_TO_KITCHEN);
        orderDTO.setOrderPositions(Set.of(orderPositionDTO));

        KitchenOrder newKitchenOrder = new KitchenOrder();
        newKitchenOrder.setKitchenOrderId(1L);
        newKitchenOrder.setStatus(OrderStatus.SENT_TO_KITCHEN);
        newKitchenOrder.setOrderToDishes(Set.of(orderToDish));

        when(kitchenOrderMapstructMapper.toKitchenOrder(orderDTO)).thenReturn(newKitchenOrder);
        doNothing().when(kitchenOrderMapper).insertKitchenOrder(newKitchenOrder);

        // when
        kitchenOrderServiceImpl.createKitchenOrderFromOrderDTO(orderDTO);

        // then
        assertEquals(1L, orderToDish.getKitchenOrderId());
        verify(kitchenOrderMapper).batchInsertOrderToDish(Set.of(orderToDish));
        Set<DishBalanceUpdateDTO> expectedBalanceUpdates =
                Set.of(new DishBalanceUpdateDTO(1L, -2));
        verify(dishServiceImpl).batchUpdateDishBalances(expectedBalanceUpdates);
    }


    /**
     * Проверяет успешную валидацию заказа.
     * given: Заказ с достаточным количеством блюд.
     * when: Вызывается метод validateOrder.
     * then: Возвращается true.
     */
    @Test
    @DisplayName("Валидация заказа - успешно")
    void validateOrder_Success() {
        // given
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(1L);
        menuDTO.setName("Pizza");
        menuDTO.setCost(100.0);

        OrderPositionDTO orderPositionDTO = new OrderPositionDTO();
        orderPositionDTO.setMenu(menuDTO);
        orderPositionDTO.setQuantity(2);

        OrderValidationDTO validationDTO = new OrderValidationDTO
                (kitchenOrder.getKitchenOrderId(),
                        Set.of(orderPositionDTO));

        Dish dish = new Dish();
        dish.setDishId(1L);
        dish.setShortName("Pizza");
        dish.setBalance(5);

        when(dishServiceImpl.getDishesByIds(Set.of(1L))).thenReturn(List.of(dish));

        // when
        Boolean result = kitchenOrderServiceImpl.validateOrder(validationDTO);

        // then
        assertTrue(result);
    }




    /**
     * Проверяет валидацию заказа с недостаточным количеством блюд.
     * given: Заказ с количеством превышающим доступный баланс.
     * when: Вызывается метод validateOrder.
     * then: Выбрасывается IllegalArgumentException.
     */
    @Test
    @DisplayName("Валидация заказа - недостаток блюд")
    void validateOrder_InsufficientDishQuantity() {
        // given
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(1L);
        menuDTO.setName("Pizza");
        menuDTO.setCost(100.0);

        OrderPositionDTO orderPositionDTO = new OrderPositionDTO();
        orderPositionDTO.setMenu(menuDTO);
        orderPositionDTO.setQuantity(10);

        OrderValidationDTO validationDTO = new OrderValidationDTO
                (kitchenOrder.getKitchenOrderId(),
                        Set.of(orderPositionDTO));

        Dish dish = new Dish();
        dish.setDishId(1L);
        dish.setShortName("Burger");
        dish.setBalance(3);

        when(dishServiceImpl.getDishesByIds(Set.of(1L))).thenReturn(List.of(dish));

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                kitchenOrderServiceImpl.validateOrder(validationDTO));

        assertTrue(ex.getMessage().contains("требуется: 10"));    }
}