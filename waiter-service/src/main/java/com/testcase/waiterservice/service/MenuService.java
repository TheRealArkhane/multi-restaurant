package com.testcase.waiterservice.service;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Интерфейс сервиса для управления позициями меню.
 * Предоставляет методы для получения всех позиций меню и поиска позиции меню по идентификатору.
 */
public interface MenuService {

    /**
     * Возвращает список всех позиций меню.
     *
     * @return список {@link MenuDTO} всех позиций меню
     */
    List<MenuDTO> getAllMenus();

    /**
     * Возвращает позицию меню по идентификатору.
     *
     * @param id идентификатор позиции меню
     * @return {@link MenuDTO} найденной позиции меню
     * @throws MenuPositionNotFoundException если позиция меню с указанным ID не найдена
     */
    MenuDTO getMenuById(Long id);


    /**
     * Возвращает список всех позиций меню постранично.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @return список {@link MenuDTO} всех позиций меню соответствующих размеров
     */
    Page<MenuDTO> getMenusPage(int page, int size);
}
