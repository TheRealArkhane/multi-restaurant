package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import com.testcase.waiterservice.entity.Waiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки маппера {@link OrderMapper}.
 * Тестирует преобразование сущности {@link Order} в DTO {@link OrderDTO} и создание новой сущности заказа.
 * При этом замокаем зависимость {@link OrderPositionMapper}, чтобы при маппинге позиций возвращался заранее заданный {@link OrderPositionDTO}
 * с вложенным {@link MenuDTO}.
 */
@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {

    @Mock
    private OrderPositionMapper orderPositionMapper;

    @Mock
    private WaiterMapper waiterMapper;

    @InjectMocks
    private OrderMapperImpl orderMapper;


    private Menu menu;
    private MenuDTO menuDTO;
    private Waiter waiter;
    private WaiterDTO waiterDTO;
    private OrderPosition orderPosition;
    private OrderPositionDTO orderPositionDTO;
    private Order order;

    @BeforeEach
    void init() {
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Пицца");
        menu.setCost(12.5);

        menuDTO = new MenuDTO();
        menuDTO.setId(1L);
        menuDTO.setName("Пицца");
        menuDTO.setCost(12.5);

        waiter = new Waiter();
        waiter.setId(10L);
        waiter.setName("Иван");
        waiter.setEmploymentDate(OffsetDateTime.now());

        waiterDTO = new WaiterDTO();
        waiterDTO.setId(10L);
        waiterDTO.setName("Иван");
        waiterDTO.setEmploymentDate(waiter.getEmploymentDate());

        orderPosition = new OrderPosition();
        orderPosition.setId(100L);
        orderPosition.setQuantity(2);
        orderPosition.setMenu(menu);

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PREPARING);
        order.setCreateDateTime(OffsetDateTime.now());
        order.setTableNumber("A12");
        order.setWaiter(waiter);
        orderPosition.setOrder(order);
        order.setOrderPositions(Set.of(orderPosition));

        orderPositionDTO = new OrderPositionDTO();
        orderPositionDTO.setOrderId(order.getId());
        orderPositionDTO.setQuantity(orderPosition.getQuantity());
        orderPositionDTO.setMenu(menuDTO);
    }

    /**
     * Проверяет успешное преобразование объекта {@link Order} в {@link OrderDTO}.
     */
    @Test
    @DisplayName("Преобразование Order в OrderDTO")
    void toOrderDTO_Success() {
        // given
        when(waiterMapper.toWaiterDTO(waiter)).thenReturn(waiterDTO);
        when(orderPositionMapper.toOrderPositionDTO(orderPosition)).thenReturn(orderPositionDTO);

        // when
        OrderDTO orderDTO = orderMapper.toOrderDTO(order);

        // then
        assertNotNull(orderDTO, "OrderDTO не должен быть null");
        assertEquals(order.getId(), orderDTO.getId(), "ID заказа должен совпадать");
        assertEquals(order.getTableNumber(), orderDTO.getTableNumber(), "Номер стола должен совпадать");
        assertEquals(orderDTO.getOrderPositions(), Set.of(orderPositionDTO));
    }

    /**
     * Проверяет успешное преобразование списка объектов {@link Order} в список {@link OrderDTO}.
     */
    @Test
    @DisplayName("Преобразование списка Order в список OrderDTO")
    void toOrderDTOList_Success() {
        // given
        when(orderPositionMapper.toOrderPositionDTO(orderPosition)).thenReturn(orderPositionDTO);

        // when
        List<OrderDTO> dtoList = orderMapper.toOrderDTOList(List.of(order));

        // then
        assertNotNull(dtoList, "Список OrderDTO не должен быть null");
        assertEquals(1, dtoList.size(), "Размер списка должен быть равен 1");
    }

    /**
     * Проверяет успешное создание новой OrderPosition на основе входных параметров.
     */
    @Test
    @DisplayName("Создание OrderPosition из параметров - успешное")
    void setOrderPositionWhileCreating_Success() {
        // given
        Order order = new Order();
        order.setId(33L);
        Integer quantity = 3;

        // when
        Order result = orderMapper.setOrderWhileCreating(
                new OrderCreateRequestDTO(waiter.getId(),
                        order.getTableNumber()), waiter);

        // then
        assertNotNull(result, "OrderPosition не должен быть null");
        assertNull(result.getId(), "ID должен быть null при создании");
        assertEquals(result.getStatus(), OrderStatus.PREPARING, "Статус должен инициализироваться как PREPARING");
    }
}