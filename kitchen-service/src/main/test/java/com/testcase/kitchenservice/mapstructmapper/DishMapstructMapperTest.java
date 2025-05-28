package java.com.testcase.kitchenservice.mapstructmapper;

import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.entity.Dish;
import com.testcase.kitchenservice.mapstruct.mapper.DishMapstructMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тестовый класс для проверки маппера {@link DishMapstructMapper}.
 * Тесты проверяют корректность преобразования объекта {@link Dish} в {@link DishDTO}
 * и списка объектов {@link Dish} в список DTO {@link DishDTO}.
 */
public class DishMapstructMapperTest {

    private final DishMapstructMapper dishMapper = Mappers.getMapper(DishMapstructMapper.class);

    /**
     * Проверяет успешное преобразование объекта Dish в DishDTO.
     * given: Создан объект Dish с заполненными полями.
     * when: Вызывается метод toDishDTO.
     * then: Проверяется соответствие всех полей DTO исходному объекту.
     */
    @Test
    @DisplayName("Преобразование Dish в DishDTO")
    void toDishDTO_Success() {
        // given
        Dish dish = new Dish();
        dish.setDishId(1L);
        dish.setBalance(100);
        dish.setShortName("Pizza");
        dish.setDishComposition("Спагетти, томатная паста, говяжий фарш, сыр Пармезан");

        // when
        DishDTO dishDTO = dishMapper.toDishDTO(dish);

        // then
        assertNotNull(dishDTO, "DTO не должен быть null");
        assertEquals(dish.getDishId(), dishDTO.getDishId(), "ID блюда должно совпадать");
        assertEquals(dish.getBalance().longValue(), dishDTO.getBalance(), "Баланс должен совпадать");
        assertEquals(dish.getShortName(), dishDTO.getShortName(), "Краткое название должно совпадать");
        assertEquals(dish.getDishComposition(), dishDTO.getDishComposition(), "Состав должен совпадать");
    }

    /**
     * Проверяет успешное преобразование списка объектов Dish в список DTO.
     * given: Создан список из двух объектов Dish с разными параметрами.
     * when: Вызывается метод toDishDTOList.
     * then: Проверяется размер списка и корректность преобразования каждого элемента.
     */
    @Test
    @DisplayName("Преобразование списка Dish в список DTO")
    void toDishDTOList_Success() {
        // given
        Dish dish1 = new Dish();
        dish1.setDishId(1L);
        dish1.setBalance(50);
        dish1.setShortName("Паста");
        dish1.setDishComposition("Паста, сливочный соус, грибы");

        Dish dish2 = new Dish();
        dish2.setDishId(2L);
        dish2.setBalance(0);
        dish2.setShortName("Суп");
        dish2.setDishComposition("Картофель, курица, морковь");

        List<Dish> dishes = List.of(dish1, dish2);

        // when
        List<DishDTO> dishDTOs = dishMapper.toDishDTOList(dishes);

        // then
        assertNotNull(dishDTOs, "Список DTO не должен быть null");
        assertEquals(2, dishDTOs.size(), "Размер списка должен быть равен 2");

        // Проверка первого элемента
        DishDTO dto1 = dishDTOs.getFirst();
        assertEquals(dish1.getDishId(), dto1.getDishId(), "ID первого блюда");
        assertEquals(dish1.getBalance().longValue(), dto1.getBalance(), "Баланс первого блюда");
        assertEquals(dish1.getShortName(), dto1.getShortName(), "Название первого блюда");
        assertEquals(dish1.getDishComposition(), dto1.getDishComposition(), "Состав первого блюда");

        // Проверка второго элемента
        DishDTO dto2 = dishDTOs.get(1);
        assertEquals(dish2.getDishId(), dto2.getDishId(), "ID второго блюда");
        assertEquals(dish2.getBalance().longValue(), dto2.getBalance(), "Баланс второго блюда");
        assertEquals(dish2.getShortName(), dto2.getShortName(), "Название второго блюда");
        assertEquals(dish2.getDishComposition(), dto2.getDishComposition(), "Состав второго блюда");
    }
}
