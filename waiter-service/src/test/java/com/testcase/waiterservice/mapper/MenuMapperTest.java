package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.entity.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки маппера {@link MenuMapper}.
 * Тесты проверяют корректность преобразования объекта {@link Menu} в {@link MenuDTO}
 * и списка объектов {@link Menu} в список DTO {@link MenuDTO}.
 */

public class MenuMapperTest {

    // Создаем экземпляр маппера с использованием MapStruct
    private final MenuMapper menuMapper = Mappers.getMapper(MenuMapper.class);

    /**
     * Проверяет успешное преобразование объекта Menu в MenuDTO.
     * given: Создан объект Menu с заданными параметрами (id, name, cost).
     * when: Вызывается метод toMenuDTO.
     * then: Проверяется, что полученный MenuDTO содержит те же значения, что и исходный объект.
     */
    @Test
    @DisplayName("Преобразование Menu в MenuDTO")
    void toMenuDTO_Success() {
        // given
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Пицца");
        menu.setCost(12.5);

        // when
        MenuDTO menuDTO = menuMapper.toMenuDTO(menu);

        // then
        assertNotNull(menuDTO, "MenuDTO не должен быть null");
        assertEquals(menu.getId(), menuDTO.getId(), "ID блюда должен совпадать");
        assertEquals(menu.getName(), menuDTO.getName(), "Название блюда должно совпадать");
        assertEquals(menu.getCost(), menuDTO.getCost(), "Стоимость блюда должна совпадать");
    }

    /**
     * Проверяет успешное преобразование списка объектов Menu в список DTO MenuDTO.
     * given: Создан список объектов Menu.
     * when: Вызывается метод toMenuDTOList.
     * then: Проверяется, что размер списка DTO соответствует размеру исходного списка и каждый элемент корректно преобразован.
     */
    @Test
    @DisplayName("Преобразование списка Menu в список MenuDTO")
    void toMenuDTOList_Success() {
        // given
        Menu menu1 = new Menu();
        menu1.setId(1L);
        menu1.setName("Пицца");
        menu1.setCost(12.5);

        Menu menu2 = new Menu();
        menu2.setId(2L);
        menu2.setName("Паста");
        menu2.setCost(10.0);

        List<Menu> menus = List.of(menu1, menu2);

        // when
        List<MenuDTO> menusDTO = menuMapper.toMenuDTOList(menus);

        // then
        assertNotNull(menusDTO, "Список MenuDTO не должен быть null");
        assertEquals(2, menusDTO.size(), "Размер списка MenuDTO должен быть равен 2");

        MenuDTO dto1 = menusDTO.get(0);
        MenuDTO dto2 = menusDTO.get(1);

        // Проверяем преобразование первого элемента
        assertEquals(menu1.getId(), dto1.getId(), "ID первого блюда должен совпадать");
        assertEquals(menu1.getName(), dto1.getName(), "Название первого блюда должно совпадать");
        assertEquals(menu1.getCost(), dto1.getCost(), "Стоимость первого блюда должна совпадать");

        // Проверяем преобразование второго элемента
        assertEquals(menu2.getId(), dto2.getId(), "ID второго блюда должен совпадать");
        assertEquals(menu2.getName(), dto2.getName(), "Название второго блюда должно совпадать");
        assertEquals(menu2.getCost(), dto2.getCost(), "Стоимость второго блюда должна совпадать");
    }
}
