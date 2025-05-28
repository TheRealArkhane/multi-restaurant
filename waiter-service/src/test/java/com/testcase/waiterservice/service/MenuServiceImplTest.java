package com.testcase.waiterservice.service;


import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.mapper.MenuMapper;
import com.testcase.waiterservice.repository.MenuRepository;
import com.testcase.waiterservice.service.impl.MenuServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для класса {@link MenuServiceImpl}, покрывающие базовые сценарии получения меню.
 */
@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuServiceImpl menuServiceImpl;

    private Menu menu;
    private MenuDTO menuDTO;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Пицца Маргарита");
        menu.setCost(12.99);

        menuDTO = new MenuDTO();
        menuDTO.setId(menu.getId());
        menuDTO.setName(menu.getName());
        menuDTO.setCost(menu.getCost());
    }

    /**
     * Проверяет получение всех позиций меню.
     * given: Настроенные моки репозитория (возвращающие список позиций) и маппера.
     * when: Вызывается метод getAllMenus.
     * then: Возвращается список DTO, проверяется взаимодействие с репозиторием и маппером.
     */
    @Test
    @DisplayName("Успешное получение всего меню")
    void getAllMenus_Success() {
        // given
        List<Menu> menus = List.of(menu);
        List<MenuDTO> menusDTO = List.of(menuDTO);

        when(menuRepository.findAll()).thenReturn(menus);
        when(menuMapper.toMenuDTOList(menus)).thenReturn(menusDTO);

        // when
        List<MenuDTO> result = menuServiceImpl.getAllMenus();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        MenuDTO firstItem = result.getFirst();
        assertEquals(menuDTO.getId(), firstItem.getId());
        assertEquals(menuDTO.getName(), firstItem.getName());
        assertEquals(menuDTO.getCost(), firstItem.getCost());

        verify(menuRepository).findAll();
        verify(menuMapper).toMenuDTOList(menus);
    }

    /**
     * Проверяет получение позиции меню по существующему ID.
     * given: Существующий ID позиции и настроенные моки репозитория и маппера.
     * when: Вызывается метод getMenuById с существующим ID.
     * then: Возвращается корректный DTO, проверяется взаимодействие с зависимостями.
     */
    @Test
    @DisplayName("Успешное получение позиции меню по ID")
    void getMenuById_Success() {
        // given
        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));
        when(menuMapper.toMenuDTO(menu)).thenReturn(menuDTO);

        // when
        MenuDTO result = menuServiceImpl.getMenuById(menu.getId());

        // then
        assertNotNull(result);
        assertEquals(menuDTO.getId(), result.getId());
        assertEquals(menuDTO.getName(), result.getName());
        assertEquals(menuDTO.getCost(), result.getCost());

        verify(menuRepository).findById(menu.getId());
        verify(menuMapper).toMenuDTO(menu);
    }

    /**
     * Проверяет обработку случая отсутствия позиции меню.
     * given: Несуществующий ID позиции и настроенный мок репозитория.
     * when: Вызывается метод getMenuById с несуществующим ID.
     * then: Выбрасывается исключение MenuPositionNotFoundException с корректным сообщением.
     */
    @Test
    @DisplayName("Получение несуществующей позиции меню")
    void getMenuById_MenuNotFound() {
        // given
        Long nonExistentId = 999L;
        when(menuRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when
        MenuPositionNotFoundException ex = assertThrows(MenuPositionNotFoundException.class,
                () -> menuServiceImpl.getMenuById(nonExistentId));

        // then
        assertEquals(String.format("Позиция меню с id: '%d' не найдена", nonExistentId), ex.getMessage());
        verify(menuRepository).findById(nonExistentId);
    }
}