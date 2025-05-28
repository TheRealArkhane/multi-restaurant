package com.testcase.waiterservice.mapper;

import com.testcase.commondto.waiterservice.Sex;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.entity.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки маппера {@link WaiterMapper}.
 * Тесты проверяют корректность преобразования объекта {@link Waiter} в {@link WaiterDTO}
 * и списка объектов {@link Waiter} в список DTO {@link WaiterDTO}.
 */
public class WaiterMapperTest {

    private final WaiterMapper waiterMapper = Mappers.getMapper(WaiterMapper.class);

    /**
     * Проверяет успешное преобразование объекта Waiter в WaiterDTO.
     * given: Создан объект Waiter с заданными параметрами (id, name, employmentDate, sex).
     * when: Вызывается метод toWaiterDTO.
     * then: Проверяется, что полученный DTO содержит те же значения, что и исходный объект.
     */
    @Test
    @DisplayName("Преобразование Waiter в WaiterDTO")
    void toWaiterDTO_Success() {
        // given
        Waiter waiter = new Waiter();
        waiter.setId(1L);
        waiter.setName("Victor");
        waiter.setEmploymentDate(OffsetDateTime.parse("2025-04-06T13:44:58.760Z"));
        waiter.setSex(Sex.MALE);

        // when
        WaiterDTO waiterDTO = waiterMapper.toWaiterDTO(waiter);

        // then
        assertNotNull(waiterDTO, "DTO не должен быть null");
        assertEquals(waiter.getId(), waiterDTO.getId(), "ID должен совпадать");
        assertEquals(waiter.getName(), waiterDTO.getName(), "Имя должно совпадать");
        assertEquals(waiter.getEmploymentDate(), waiterDTO.getEmploymentDate(), "Дата трудоустройства должна совпадать");
        assertEquals(waiter.getSex(), waiterDTO.getSex(), "Пол должен совпадать");
    }

    /**
     * Проверяет успешное преобразование списка объектов Waiter в список DTO.
     * given: Создан список из двух объектов Waiter с разными параметрами.
     * when: Вызывается метод toWaiterDTOList.
     * then: Проверяется размер списка и корректность преобразования каждого элемента.
     */
    @Test
    @DisplayName("Преобразование списка Waiters в список DTO")
    void toWaiterDTOList_Success() {
        // given
        Waiter waiter1 = new Waiter();
        waiter1.setId(1L);
        waiter1.setName("Victor");
        waiter1.setEmploymentDate(OffsetDateTime.parse("2023-01-15T09:00:00Z"));
        waiter1.setSex(Sex.MALE);

        Waiter waiter2 = new Waiter();
        waiter2.setId(2L);
        waiter2.setName("Anna");
        waiter2.setEmploymentDate(OffsetDateTime.parse("2024-05-20T14:30:00Z"));
        waiter2.setSex(Sex.FEMALE);

        List<Waiter> waiters = List.of(waiter1, waiter2);

        // when
        List<WaiterDTO> waiterDTOs = waiterMapper.toWaiterDTOList(waiters);

        // then
        assertNotNull(waiterDTOs, "Список DTO не должен быть null");
        assertEquals(2, waiterDTOs.size(), "Размер списка должен быть равен 2");

        // Проверка первого элемента
        WaiterDTO dto1 = waiterDTOs.getFirst();
        assertEquals(waiter1.getId(), dto1.getId(), "ID первого официанта");
        assertEquals(waiter1.getName(), dto1.getName(), "Имя первого официанта");
        assertEquals(waiter1.getEmploymentDate(), dto1.getEmploymentDate(), "Дата первого официанта");
        assertEquals(waiter1.getSex(), dto1.getSex(), "Пол первого официанта");

        // Проверка второго элемента
        WaiterDTO dto2 = waiterDTOs.get(1);
        assertEquals(waiter2.getId(), dto2.getId(), "ID второго официанта");
        assertEquals(waiter2.getName(), dto2.getName(), "Имя второго официанта");
        assertEquals(waiter2.getEmploymentDate(), dto2.getEmploymentDate(), "Дата второго официанта");
        assertEquals(waiter2.getSex(), dto2.getSex(), "Пол второго официанта");
    }
}
