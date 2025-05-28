package java.com.testcase.kitchenservice.mapstructmapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.kitchenservice.dto.OrderToDishDTO;
import com.testcase.kitchenservice.entity.OrderToDish;
import com.testcase.kitchenservice.mapstruct.mapper.OrderToDishMapstructMapper;
import com.testcase.kitchenservice.mapstruct.mapper.OrderToDishMapstructMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тестовый класс для проверки маппера {@link OrderToDishMapstructMapper}.
 * Тестирует преобразование сущности {@link OrderToDish} в DTO {@link OrderToDishDTO},
 * преобразование списков и создание сущностей из DTO.
 */
@ExtendWith(MockitoExtension.class)
public class OrderToDishMapstructMapperTest {

    @InjectMocks
    private OrderToDishMapstructMapperImpl orderToDishMapper;

    private OrderToDish orderToDish;
    private OrderPositionDTO orderPositionDTO;

    @BeforeEach
    void setUp() {
        orderToDish = new OrderToDish();
        orderToDish.setKitchenOrderId(1L);
        orderToDish.setDishId(100L);
        orderToDish.setDishesCount(3);

        orderPositionDTO = new OrderPositionDTO();
        orderPositionDTO.setOrderId(2L);
        orderPositionDTO.setMenu(new MenuDTO());
        orderPositionDTO.getMenu().setId(200L);
        orderPositionDTO.setQuantity(5);
    }

    /**
     * Проверяет преобразование OrderToDish в OrderToDishDTO.
     * <p>given: Сущность OrderToDish с заполненными полями.
     * <p>when: Вызывается метод toOrderToDishDTO.
     * <p>then: Все поля DTO соответствуют значениям сущности.
     */
    @Test
    @DisplayName("Преобразование OrderToDish в DTO - успешно")
    void toOrderToDishDTO_Success() {
        // when
        OrderToDishDTO dto = orderToDishMapper.toOrderToDishDTO(orderToDish);

        // then
        assertNotNull(dto, "DTO не должен быть null");
        assertEquals(orderToDish.getKitchenOrderId(), dto.getKitchenOrderId(), "ID заказа должен совпадать");
        assertEquals(orderToDish.getDishId(), dto.getDishId(), "ID блюда должен совпадать");
        assertEquals(orderToDish.getDishesCount(), dto.getDishesCount(), "Количество должно совпадать");
    }

    /**
     * Проверяет преобразование списка OrderToDish в список DTO.
     * <p>given: Список из двух сущностей.
     * <p>when: Вызывается метод toOrderToDishDTOList.
     * <p>then: Размер списка и значения полей соответствуют ожиданиям.
     */
    @Test
    @DisplayName("Преобразование списка OrderToDish в список DTO - успешно")
    void toOrderToDishDTOList_Success() {
        // given
        OrderToDish secondEntity = new OrderToDish(3L, 300L, 2);
        List<OrderToDish> entities = List.of(orderToDish, secondEntity);

        // when
        List<OrderToDishDTO> dtos = orderToDishMapper.toOrderToDishDTOList(entities);

        // then
        assertEquals(2, dtos.size(), "Размер списка должен быть 2");

        OrderToDishDTO firstDto = dtos.getFirst();
        assertEquals(orderToDish.getKitchenOrderId(), firstDto.getKitchenOrderId());
        assertEquals(orderToDish.getDishId(), firstDto.getDishId());
        assertEquals(orderToDish.getDishesCount(), firstDto.getDishesCount());

        OrderToDishDTO secondDto = dtos.get(1);
        assertEquals(secondEntity.getKitchenOrderId(), secondDto.getKitchenOrderId());
        assertEquals(secondEntity.getDishId(), secondDto.getDishId());
        assertEquals(secondEntity.getDishesCount(), secondDto.getDishesCount());
    }

    /**
     * Проверяет создание OrderToDish из OrderPositionDTO.
     * <p>given: DTO с заполненными полями orderId, menu.id и quantity.
     * <p>when: Вызывается метод toOrderToDish.
     * <p>then: Сущность содержит преобразованные значения в соответствии с маппингом.
     */
    @Test
    @DisplayName("Создание OrderToDish из OrderPositionDTO - успешно")
    void toOrderToDish_FromOrderPositionDTO_Success() {
        // when
        OrderToDish entity = orderToDishMapper.toOrderToDish(orderPositionDTO);

        // then
        assertNotNull(entity, "Сущность не должна быть null");
        assertEquals(orderPositionDTO.getOrderId(), entity.getKitchenOrderId(), "ID заказа должен быть преобразован");
        assertEquals(orderPositionDTO.getMenu().getId(), entity.getDishId(), "ID блюда должен быть взят из меню");
        assertEquals(orderPositionDTO.getQuantity(), entity.getDishesCount(), "Количество должно совпадать");
    }

    /**
     * Проверяет преобразование списка OrderPositionDTO в список OrderToDish.
     * <p>given: Список из двух DTO.
     * <p>when: Вызывается метод toOrderToDishes.
     * <p>then: Все элементы списка корректно преобразованы.
     */
    @Test
    @DisplayName("Преобразование списка OrderPositionDTO в список сущностей - успешно")
    void toOrderToDishes_FromDTOList_Success() {
        // given
        OrderPositionDTO secondDTO = new OrderPositionDTO();
        secondDTO.setOrderId(4L);
        secondDTO.setMenu(new MenuDTO());
        secondDTO.getMenu().setId(400L);
        secondDTO.setQuantity(1);

        List<OrderPositionDTO> dtos = List.of(orderPositionDTO, secondDTO);

        // when
        List<OrderToDish> entities = orderToDishMapper.toOrderToDishes(dtos);

        // then
        assertEquals(2, entities.size(), "Размер списка должен быть 2");

        OrderToDish firstEntity = entities.getFirst();
        assertEquals(orderPositionDTO.getOrderId(), firstEntity.getKitchenOrderId());
        assertEquals(orderPositionDTO.getMenu().getId(), firstEntity.getDishId());
        assertEquals(orderPositionDTO.getQuantity(), firstEntity.getDishesCount());

        OrderToDish secondEntity = entities.get(1);
        assertEquals(secondDTO.getOrderId(), secondEntity.getKitchenOrderId());
        assertEquals(secondDTO.getMenu().getId(), secondEntity.getDishId());
        assertEquals(secondDTO.getQuantity(), secondEntity.getDishesCount());
    }
}
