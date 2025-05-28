package java.com.testcase.kitchenservice.service;

import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.entity.Dish;
import com.testcase.kitchenservice.exception.DishNotFoundException;
import com.testcase.kitchenservice.mapper.DishMapper;
import com.testcase.kitchenservice.mapstruct.mapper.DishMapstructMapper;
import com.testcase.kitchenservice.service.impl.DishServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для класса {@link DishServiceImpl}, покрывающие базовые сценарии получения блюд.
 */
@ExtendWith(MockitoExtension.class)
public class DishServiceImplTest {

    @Mock
    private DishMapper dishMapper;
    @Mock
    private DishMapstructMapper dishMapstructMapper;

    @InjectMocks
    private DishServiceImpl dishServiceImpl;

    private Dish dish;
    private DishDTO dishDTO;

    @BeforeEach
    void setUp() {
        dish = new Dish();
        dish.setDishId(1L);
        dish.setBalance(100);
        dish.setShortName("Pizza");
        dish.setDishComposition("Tomato, Cheese, Flour");

        dishDTO = new DishDTO();
        dishDTO.setDishId(1L);
        dishDTO.setBalance(100L);
        dishDTO.setShortName("Pizza");
        dishDTO.setDishComposition("Tomato, Cheese, Flour");
    }


    /**
     * Проверяет успешное получение всех блюд.
     * given: Настроенные моки маппера (возвращающие список блюд) и мапстуркта.
     * when: Вызывается метод getAllDishes.
     * then: Возвращается список DTO, проверяется взаимодействие с зависимостями.
     */
    @Test
    @DisplayName("Успешное получение всех блюд")
    void getAllDishes_Success() {
        // given
        List<Dish> dishes = List.of(dish);
        List<DishDTO> dishDTOList = List.of(dishDTO);
        when(dishMapper.getAllDishes()).thenReturn(dishes);
        when(dishMapstructMapper.toDishDTOList(dishes)).thenReturn(dishDTOList);

        // when
        List<DishDTO> result = dishServiceImpl.getAllDishes();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dishDTO.getDishId(), result.getFirst().getDishId());
        verify(dishMapper).getAllDishes();
        verify(dishMapstructMapper).toDishDTOList(dishes);
    }


    /**
     * Проверяет получение блюда по существующему ID.
     * given: Существующий ID блюда и настроенные моки маппера.
     * when: Вызывается метод getDishDTOById с валидным ID.
     * then: Возвращается корректный DTO, проверяются вызовы зависимостей.
     */
    @Test
    @DisplayName("Успешное получение блюда по ID")
    void getDishDTOById_Success() {
        // given
        Long dishId = dish.getDishId();
        when(dishMapper.getDishById(dishId)).thenReturn(Optional.of(dish));
        when(dishMapstructMapper.toDishDTO(dish)).thenReturn(dishDTO);

        // when
        DishDTO result = dishServiceImpl.getDishDTOById(dishId);

        // then
        assertNotNull(result);
        assertEquals(dishDTO.getDishId(), result.getDishId());
        verify(dishMapper).getDishById(dishId);
        verify(dishMapstructMapper).toDishDTO(dish);
    }


    /**
     * Проверяет обработку запроса несуществующего блюда.
     * given: Несуществующий ID блюда.
     * when: Вызывается метод getDishDTOById с невалидным ID.
     * then: Выбрасывается DishNotFoundException.
     */
    @Test
    @DisplayName("Получение несуществующего блюда")
    void getDishDTOById_DishNotFound() {
        // given
        Long dishId = 999L;
        when(dishMapper.getDishById(dishId)).thenReturn(Optional.empty());

        // when
        DishNotFoundException ex = assertThrows(DishNotFoundException.class, () ->
                dishServiceImpl.getDishDTOById(dishId));

        // then
        assertEquals(ex.getMessage(), String.format("Блюдо с id: '%d' не найдено", dishId));
    }


    /**
     * Проверяет получение сущности Dish по ID.
     * given: Существующий ID блюда.
     * when: Вызывается метод getDishById.
     * then: Возвращается сущность Dish, проверяется вызов маппера.
     */
    @Test
    @DisplayName("Получение объекта Dish по ID")
    void getDishById_Success() {
        // given
        Long dishId = dish.getDishId();
        when(dishMapper.getDishById(dishId)).thenReturn(Optional.of(dish));

        // when
        Dish result = dishServiceImpl.getDishById(dishId);

        // then
        assertNotNull(result);
        assertEquals(dish.getDishId(), result.getDishId());
        verify(dishMapper).getDishById(dishId);
    }


    /**
     * Проверяет обновление баланса блюда.
     * given: ID блюда и новое значение баланса.
     * when: Вызывается метод updateDishBalance.
     * then: Проверяется вызов маппера с корректными параметрами.
     */
    @Test
    @DisplayName("Обновление баланса блюда")
    void updateDishBalance_Success() {
        // given
        Long dishId = dish.getDishId();
        Integer newBalance = 50;

        // when
        dishServiceImpl.updateDishBalance(dishId, newBalance);

        // then
        verify(dishMapper).updateDishBalance(eq(dishId), eq(newBalance));
    }
}