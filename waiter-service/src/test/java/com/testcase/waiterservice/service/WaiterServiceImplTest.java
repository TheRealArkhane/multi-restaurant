package com.testcase.waiterservice.service;


import com.testcase.commondto.waiterservice.Sex;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.entity.Waiter;
import com.testcase.waiterservice.exception.WaiterNotFoundException;
import com.testcase.waiterservice.mapper.WaiterMapper;
import com.testcase.waiterservice.repository.WaiterRepository;
import com.testcase.waiterservice.service.impl.WaiterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для {@link WaiterServiceImpl}, проверяющие корректность получения
 * информации о официантах, включая получение по ID и списка всех официантов.
 */
@ExtendWith(MockitoExtension.class)
public class WaiterServiceImplTest {

    @Mock
    private WaiterRepository waiterRepository;

    @Mock
    private WaiterMapper waiterMapper;

    @InjectMocks
    private WaiterServiceImpl waiterServiceImpl;

    private Waiter waiter;
    private WaiterDTO waiterDTO;

    @BeforeEach
    void setUp() {
        waiter = new Waiter();
        waiter.setId(1L);
        waiter.setName("Иван Иванов");
        waiter.setEmploymentDate(OffsetDateTime.parse("2023-01-15T09:00:00Z"));
        waiter.setSex(Sex.MALE);

        waiterDTO = new WaiterDTO();
        waiterDTO.setId(waiter.getId());
        waiterDTO.setName(waiter.getName());
        waiterDTO.setEmploymentDate(waiter.getEmploymentDate());
        waiterDTO.setSex(waiter.getSex());
    }


    /**
     * Проверяет успешное получение официанта по его ID.
     * given: Существующий ID официанта и настроенные моки репозитория и маппера.
     * when: Вызывается метод getWaiterById с существующим ID.
     * then: Возвращается корректный DTO, проверяется взаимодействие с репозиторием и маппером.
     */
    @Test
    @DisplayName("Успешное получение официанта по ID")
    void getWaiterById_Success() {
        // given
        when(waiterRepository.findById(waiter.getId())).thenReturn(Optional.of(waiter));
        when(waiterMapper.toWaiterDTO(waiter)).thenReturn(waiterDTO);

        // when
        WaiterDTO result = waiterServiceImpl.getWaiterById(waiter.getId());

        // then
        assertNotNull(result);
        assertEquals(waiterDTO.getId(), result.getId());
        assertEquals(waiterDTO.getName(), result.getName());
        assertEquals(waiterDTO.getEmploymentDate(), result.getEmploymentDate());
        assertEquals(waiterDTO.getSex(), result.getSex());

        verify(waiterRepository).findById(waiter.getId());
        verify(waiterMapper).toWaiterDTO(waiter);
    }


    /**
     * Проверяет обработку случая отсутствия официанта по указанному ID.
     * given: Несуществующий ID официанта и настроенный мок репозитория.
     * when: Вызывается метод getWaiterById с несуществующим ID.
     * then: Выбрасывается исключение WaiterNotFoundException с корректным сообщением.
     */
    @Test
    @DisplayName("Получение официанта по несуществующему ID")
    void getWaiterById_WaiterNotFound() {
        // given
        Long nonExistentId = 999L;
        when(waiterRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when
        WaiterNotFoundException ex = assertThrows(WaiterNotFoundException.class,
                () -> waiterServiceImpl.getWaiterById(nonExistentId));

        // then
        assertEquals(String.format("Официант с id: %d не найден", nonExistentId), ex.getMessage());
        verify(waiterRepository).findById(nonExistentId);
    }


    /**
     * Проверяет получение полного списка официантов.
     * given: Настроенные моки репозитория (возвращающие список) и маппера.
     * when: Вызывается метод getAllWaiters.
     * then: Возвращается список DTO, проверяется взаимодействие с репозиторием и маппером.
     */
    @Test
    @DisplayName("Получение списка всех официантов")
    void getAllWaiters_Success() {
        // given
        List<Waiter> waiters = List.of(waiter);
        List<WaiterDTO> waitersDTO = List.of(waiterDTO);

        when(waiterRepository.findAll()).thenReturn(waiters);
        when(waiterMapper.toWaiterDTOList(waiters)).thenReturn(waitersDTO);

        // when
        List<WaiterDTO> result = waiterServiceImpl.getAllWaiters();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(waiterDTO.getId(), result.getFirst().getId());
        assertEquals(waiterDTO.getName(), result.getFirst().getName());

        verify(waiterRepository).findAll();
        verify(waiterMapper).toWaiterDTOList(waiters);
    }
}