package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.entity.Order;
import com.testcase.waiterservice.entity.OrderPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки маппера {@link OrderPositionMapper}.
 * Тестирует преобразование сущности {@link OrderPosition} в DTO {@link OrderPositionDTO}
 * и создание нового объекта {@link OrderPosition} из параметров.
 */
@ExtendWith(MockitoExtension.class)
public class OrderPositionMapperTest {

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private OrderPositionMapperImpl orderPositionMapper;

    private Menu menu;
    private MenuDTO menuDTO;

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
    }


    /**
     * Проверяет успешное преобразование OrderPosition в OrderPositionDTO.
     * <p>given: OrderPosition с заполненными полями и связанным объектом Menu.
     * <p>when: Вызывается метод toOrderPositionDTO.
     * <p>then: DTO содержит правильные значения, а метод toMenuDTO замокан для возврата нужного MenuDTO.
     */
    @Test
    @DisplayName("Преобразование OrderPosition в OrderPositionDTO - успешное")
    void toOrderPositionDTO_Success() {
        // given
        when(menuMapper.toMenuDTO(menu)).thenReturn(menuDTO);

        Order order = new Order();
        order.setId(1L);
        OrderPosition orderPosition = new OrderPosition(100L, 2, order, menu);

        // when
        OrderPositionDTO dto = orderPositionMapper.toOrderPositionDTO(orderPosition);

        // then
        assertNotNull(dto, "OrderPositionDTO не должен быть null");
        assertEquals(order.getId(), dto.getOrderId(), "ID заказа должен совпадать");
        assertEquals(orderPosition.getQuantity(), dto.getQuantity(), "Количество должно совпадать");
    }

    /**
     * Проверяет успешное преобразование списка OrderPosition в список OrderPositionDTO.
     * <p>given: Список из OrderPosition.
     * <p>when: Вызывается метод toOrderPositionDTOList.
     * <p>then: Полученный список DTO корректно отображает все значения.
     */
    @Test
    @DisplayName("Преобразование списка OrderPosition в список OrderPositionDTO - успешное")
    void toOrderPositionDTOList_Success() {
        // given
        when(menuMapper.toMenuDTO(menu)).thenReturn(menuDTO);

        Order order = new Order();
        order.setId(2L);

        OrderPosition position1 = new OrderPosition(201L, 1, order, menu);

        List<OrderPosition> positions = List.of(position1);

        // when
        List<OrderPositionDTO> dtoList = orderPositionMapper.toOrderPositionDTOList(positions);

        // then
        assertNotNull(dtoList, "Список DTO не должен быть null");
        assertEquals(1, dtoList.size(), "Размер списка должен быть равен 1");

        OrderPositionDTO dto1 = dtoList.getFirst();

        assertEquals(order.getId(), dto1.getOrderId(), "ID заказа первой позиции должен совпадать");
        assertEquals(position1.getQuantity(), dto1.getQuantity(), "Количество первой позиции должно совпадать");
    }

    /**
     * Проверяет успешное создание новой OrderPosition на основе входных параметров.
     * <p>given: Меню, количество и заказ.
     * <p>when: Вызывается метод setOrderPositionWhileCreating.
     * <p>then: Полученная сущность содержит корректные ссылки и значения.
     */
    @Test
    @DisplayName("Создание OrderPosition из параметров - успешное")
    void setOrderPositionWhileCreating_Success() {
        // given
        Order order = new Order();
        order.setId(33L);

        Integer quantity = 3;

        // when
        OrderPosition result = orderPositionMapper.setOrderPositionWhileCreating(menu, quantity, order);

        // then
        assertNotNull(result, "OrderPosition не должен быть null");
        assertNull(result.getId(), "ID должен быть null при создании");
    }
}