package com.testcase.waiterservice.integration.controller;

import com.testcase.commondto.waiterservice.MenuDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест контроллера меню {@link com.testcase.waiterservice.controller.WaiterController}.
 * Проверяет эндпоинты получения всех позиций меню и получения меню по ID.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RunWaiterService.class)
@ActiveProfiles("test")
@Testcontainers
class MenuControllerIT extends IntegrationTestsBase {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;


    private String baseUrl() {
        return "http://localhost:" + port + "/menu";
    }

    /**
     * Проверяет успешное получение списка всех меню.
     * given: Имеется заполненная БД, в которой присутствуют записи о позициях меню согласно скрипту.
     * when: Выполняется HTTP GET запрос по адресу {@code /menu}.
     * then: Возвращается статус 200, список не пуст, и данные каждого меню соответствуют ожиданиям.
     */
    @Test
    @DisplayName("Получение списка всех меню - успешное")
    void shouldReturnAllMenus_Success() {
        // when
        ResponseEntity<MenuDTO[]> response = restTemplate.getForEntity(baseUrl(), MenuDTO[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MenuDTO[] menus = response.getBody();
        assertThat(menus).isNotNull();

        // Согласно скрипту, вставляются 3 позиции меню.
        assertThat(menus).hasSize(3);

        // Для примера проверим первую позицию: "Пицца Маргарита", стоимость 12.50
        MenuDTO menu1 = menus[0];
        assertThat(menu1.getId()).isEqualTo(1L);
        assertThat(menu1.getName()).isEqualTo("Пицца Маргарита");
        assertThat(menu1.getCost()).isEqualTo(12.50);
    }

    /**
     * Проверяет успешное получение позиции меню по её ID.
     * given: В БД присутствует запись о позиции меню с заданным ID (например, ID = 1, "Пицца Маргарита").
     * when: Выполняется HTTP GET запрос по адресу {@code /menu/{id}}.
     * then: Возвращается статус 200 и DTO позиции меню содержит корректные данные.
     */
    @Test
    @DisplayName("Получение позиции меню по ID - успешное")
    void shouldReturnMenuById_Success() {
        // when
        ResponseEntity<MenuDTO> response = restTemplate.getForEntity
                (baseUrl() + "/{id}", MenuDTO.class, 1L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MenuDTO menuDTO = response.getBody();
        assertThat(menuDTO).isNotNull();
        assertThat(menuDTO.getId()).isEqualTo(1L);
        assertThat(menuDTO.getName()).isEqualTo("Пицца Маргарита");
        assertThat(menuDTO.getCost()).isEqualTo(12.50);
    }
}