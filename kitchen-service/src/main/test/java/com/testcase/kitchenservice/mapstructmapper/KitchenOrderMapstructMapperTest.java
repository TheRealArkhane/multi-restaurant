package java.com.testcase.kitchenservice.mapstructmapper;

import com.testcase.commondto.UpdateOrderStatusDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.dto.OrderToDishDTO;
import com.testcase.kitchenservice.entity.KitchenOrder;
import com.testcase.kitchenservice.entity.OrderToDish;
import com.testcase.kitchenservice.mapstruct.mapper.KitchenOrderMapstructMapper;
import com.testcase.kitchenservice.mapstruct.mapper.KitchenOrderMapstructMapperImpl;
import com.testcase.kitchenservice.mapstruct.mapper.OrderToDishMapstructMapper;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


/**
 * Тестовый класс для проверки маппера {@link KitchenOrderMapstructMapper}.
 * Проверяет корректность преобразований между сущностями {@link KitchenOrder}, {@link OrderDTO}
 * и DTO {@link KitchenOrderDTO}, {@link UpdateOrderStatusDTO}.
 */
@ExtendWith(MockitoExtension.class)
public class KitchenOrderMapstructMapperTest {

    @Mock
    private OrderToDishMapstructMapper orderToDishMapper;

    @InjectMocks
    private KitchenOrderMapstructMapperImpl kitchenOrderMapper;

    private KitchenOrder kitchenOrder;
    private OrderDTO orderDTO;
    private OrderToDish orderToDish;
    private OrderToDishDTO orderToDishDTO;

    @BeforeEach
    void setUp() {
        kitchenOrder = new KitchenOrder();
        kitchenOrder.setKitchenOrderId(1L);
        kitchenOrder.setWaiterOrderId(10L);
        kitchenOrder.setStatus(OrderStatus.PREPARING);
        kitchenOrder.setCreateDateTime(OffsetDateTime.now());

        orderToDish = new OrderToDish(1L, 100L, 2);
        kitchenOrder.setOrderToDishes(Set.of(orderToDish));

        orderToDishDTO = new OrderToDishDTO();
        orderToDishDTO.setKitchenOrderId(1L);
        orderToDishDTO.setDishId(100L);
        orderToDishDTO.setDishesCount(2);

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setWaiter(new WaiterDTO());
        orderDTO.getWaiter().setId(10L);
        orderDTO.setStatus(OrderStatus.PREPARING);
        orderDTO.setCreateDateTime(kitchenOrder.getCreateDateTime());
        orderDTO.setOrderPositions(Set.of(new OrderPositionDTO()));
    }


    /**
     * Проверяет полное преобразование всех полей сущности KitchenOrder в KitchenOrderDTO.
     * given: KitchenOrder с заполненными полями и связанными позициями заказа.
     * when: Вызывается метод toKitchenOrderDTO.
     * then: Все поля DTO должны соответствовать значениям исходной сущности.
     */
    @Test
    @DisplayName("Преобразование KitchenOrder в KitchenOrderDTO")
    void toKitchenOrderDTO_AllFieldsMapped() {
        // given
        when(orderToDishMapper.toOrderToDishDTO(orderToDish)).thenReturn(orderToDishDTO);

        // when
        KitchenOrderDTO dto = kitchenOrderMapper.toKitchenOrderDTO(kitchenOrder);

        // then
        assertNotNull(dto);
        assertEquals(kitchenOrder.getKitchenOrderId(), dto.getKitchenOrderId());
        assertEquals(kitchenOrder.getWaiterOrderId(), dto.getWaiterOrderId());
        assertEquals(kitchenOrder.getStatus(), dto.getStatus());
        assertEquals(kitchenOrder.getCreateDateTime(), dto.getCreateDateTime());

        assertNotNull(dto.getOrderToDishes());
        assertEquals(1, dto.getOrderToDishes().size());
        assertEquals(orderToDishDTO, dto.getOrderToDishes().iterator().next());
    }


    /**
     * Проверяет преобразование списка KitchenOrder в список DTO.
     * given: Список из двух объектов KitchenOrder.
     * when: Вызывается метод toKitchenOrderDTOList.
     * then: Размер и порядок элементов результирующего списка должны соответствовать исходному.
     */
    @Test
    @DisplayName("Преобразование списка KitchenOrder в список DTO")
    void toKitchenOrderDTOList_Success() {
        // given
        when(orderToDishMapper.toOrderToDishDTO(orderToDish)).thenReturn(orderToDishDTO);
        KitchenOrder secondOrder = new KitchenOrder(
                2L,
                20L,
                OrderStatus.PREPARING,
                OffsetDateTime.now(), Set.of());

        // when
        List<KitchenOrderDTO> dtos = kitchenOrderMapper.toKitchenOrderDTOList(List.of(kitchenOrder, secondOrder));

        // then
        assertEquals(2, dtos.size());
        assertEquals(kitchenOrder.getKitchenOrderId(), dtos.get(0).getKitchenOrderId());
        assertEquals(secondOrder.getKitchenOrderId(), dtos.get(1).getKitchenOrderId());
    }


    /**
     * Проверяет преобразование KitchenOrder в UpdateOrderStatusDTO.
     * given: KitchenOrder с заполненными полями ID и статус.
     * when: Вызывается метод toUpdateOrderStatusDTO.
     * then: Поля ID и статус DTO должны соответствовать значениям исходной сущности.
     */
    @Test
    @DisplayName("Преобразование в UpdateOrderStatusDTO")
    void toUpdateOrderStatusDTO_Mapping() {
        // when
        UpdateOrderStatusDTO dto = kitchenOrderMapper.toUpdateOrderStatusDTO(kitchenOrder);

        // then
        assertEquals(kitchenOrder.getKitchenOrderId(), dto.getId());
        assertEquals(kitchenOrder.getStatus(), dto.getStatus());
    }


    /**
     * Проверяет преобразование OrderDTO в KitchenOrder.
     * given: OrderDTO с заполненными полями и позициями заказа.
     * when: Вызывается метод toKitchenOrder.
     * then: Все основные поля и коллекции должны быть корректно преобразованы.
     */
    @Test
    @DisplayName("Преобразование OrderDTO в KitchenOrder")
    void toKitchenOrder_FromOrderDTO() {
        // when
        KitchenOrder result = kitchenOrderMapper.toKitchenOrder(orderDTO);

        // then
        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getKitchenOrderId());
        assertEquals(orderDTO.getWaiter().getId(), result.getWaiterOrderId());
        assertEquals(orderDTO.getStatus(), result.getStatus());
        assertEquals(orderDTO.getCreateDateTime(), result.getCreateDateTime());
        assertEquals(1, result.getOrderToDishes().size());
    }
}