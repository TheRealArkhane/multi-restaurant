package com.testcase.waiterservice.service.impl;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.entity.Menu;
import com.testcase.waiterservice.exception.MenuPositionNotFoundException;
import com.testcase.waiterservice.mapper.MenuMapper;
import com.testcase.waiterservice.repository.MenuRepository;
import com.testcase.waiterservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления позициями меню.
 * Выполняет получение всех блюд и поиск блюда по идентификатору с использованием репозитория и мапперов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;


    @Override
    public List<MenuDTO> getAllMenus() {
        log.info("Получение списка всех позиций меню");
        List<MenuDTO> menus = menuMapper.toMenuDTOList(menuRepository.findAll());
        log.info("Получено {} позиций меню", menus.size());
        return menus;
    }


    @Override
    public MenuDTO getMenuById(Long id) {
        log.info("Поиск позиции меню по id: {}", id);
        MenuDTO menuDTO = menuMapper.toMenuDTO(menuRepository.findById(id)
                .orElseThrow(() -> new MenuPositionNotFoundException(
                        String.format("Позиция меню с id: '%d' не найдена", id))));
        log.info("Позиция меню c id: {} найдена", id);
        return menuDTO;
    }


    @Override
    public Page<MenuDTO> getMenusPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Menu> menusPage = menuRepository.findAll(pageable);
        return menusPage.map(menuMapper::toMenuDTO);
    }
}
