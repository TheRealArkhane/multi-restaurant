package com.testcase.waiterservice.integration.controller;

import com.testcase.commondto.waiterservice.Sex;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.RunWaiterService;
import com.testcase.waiterservice.integration.IntegrationTestsBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест контроллера официантов {@link com.testcase.waiterservice.controller.WaiterController}.
 * Проверяет эндпоинты получения всех официантов и получения официанта по ID.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RunWaiterService.class)
@ActiveProfiles("test")
@Testcontainers
class WaiterControllerIT extends IntegrationTestsBase {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/waiters";
    }


    /**
     * Проверяет возврат списка всех официантов.
     * given: в тестовой БД 2 официанта.
     * when: выполняется GET-запрос на {@code /waiters}.
     * then: HTTP 200 и массив из двух {@link WaiterDTO}, все поля каждого DTO совпадают с данными из БД.
     */
    @Test
    @DisplayName("Получение всех официантов")
    void shouldReturnAllWaiters() {
        // when
        ResponseEntity<WaiterDTO[]> response = restTemplate.getForEntity(baseUrl(), WaiterDTO[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WaiterDTO[] waiters = response.getBody();

        assertThat(waiters).isNotNull();
        assertThat(waiters).hasSize(2);

        WaiterDTO ivan = findWaiterById(waiters, 1L);
        WaiterDTO anna = findWaiterById(waiters, 2L);

        assertIvanPetrov(ivan);
        assertAnnaIvanova(anna);
    }


    /**
     * Проверяет возврат официанта по его ID.
     * given: в тестовой БД официант с ID = 1.
     * when: выполняется GET-запрос на {@code /waiters/1}.
     * then: HTTP 200 и {@link WaiterDTO} с полями, соответствующими официанту Ивану Петрову.
     */
    @Test
    @DisplayName("Получение официанта по ID")
    void shouldReturnWaiterById() {
        // when
        ResponseEntity<WaiterDTO> response = restTemplate.getForEntity(baseUrl() + "/1", WaiterDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WaiterDTO waiter = response.getBody();

        assertThat(waiter).isNotNull();
        assertIvanPetrov(waiter);
    }


    /**
     * Находит официанта в массиве по его идентификатору.
     *
     * @param waiters массив DTO официантов
     * @param id      ID искомого официанта
     * @return DTO официанта с заданным ID
     */
    private WaiterDTO findWaiterById(WaiterDTO[] waiters, Long id) {
        return List.of(waiters).stream()
                .filter(w -> id.equals(w.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найден официант с id: " + id));
    }


    /**
     * Проверяет поля DTO для официанта Иван Петров.
     *
     * @param waiter DTO официанта
     */
    private void assertIvanPetrov(WaiterDTO waiter) {
        assertThat(waiter.getId()).isEqualTo(1L);
        assertThat(waiter.getName()).isEqualTo("Иван Петров");
        assertThat(waiter.getSex()).isEqualTo(Sex.MALE);
        assertThat(waiter.getEmploymentDate()).isEqualTo(OffsetDateTime.parse("2023-01-15T09:00:00+03:00"));
    }


    /**
     * Проверяет поля DTO для официанта Анна Иванова.
     *
     * @param waiter DTO официанта
     */
    private void assertAnnaIvanova(WaiterDTO waiter) {
        assertThat(waiter.getId()).isEqualTo(2L);
        assertThat(waiter.getName()).isEqualTo("Анна Иванова");
        assertThat(waiter.getSex()).isEqualTo(Sex.FEMALE);
        assertThat(waiter.getEmploymentDate()).isEqualTo(OffsetDateTime.parse("2022-06-01T10:30:00+03:00"));
    }
}