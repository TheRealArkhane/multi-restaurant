package com.testcase.waiterservice.service;


import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.WaiterDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Интерфейс сервиса для работы с официантами.
 * Предоставляет методы для получения информации об официантах.
 */
public interface WaiterService {

    /**
     * Получает информацию о конкретном официанте по его идентификатору.
     *
     * @param id идентификатор официанта
     * @return {@link WaiterDTO} информация об официанте
     */
    WaiterDTO getWaiterById(Long id);

    /**
     * Получает список всех официантов.
     *
     * @return список {@link WaiterDTO}
     */
    List<WaiterDTO> getAllWaiters();

    /**
     * Возвращает список всех официантов постранично.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @return список {@link MenuDTO} всех официантов соответствующих размеров
     */
    Page<WaiterDTO> getWaitersPage(int page, int size);
}
