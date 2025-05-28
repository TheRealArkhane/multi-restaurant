package java.com.testcase.kitchenservice.integration.controller;

import com.testcase.kitchenservice.dto.DishDTO;
import java.com.testcase.kitchenservice.integration.IntegrationTestsBase;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест для контроллера {@code DishController}.
 * Проверяет эндпоинты получения списка блюд и получения блюда по ID.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class DishControllerIT extends IntegrationTestsBase {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/dishes";
    }

    /**
     * Проверяет получение всех блюд.
     * given: в БД есть несколько блюд.
     * when: выполняется GET на {@code /dishes}.
     * then: HTTP 200 и список DTO блюд.
     */
    @Test
    @DisplayName("Получение всех блюд - успешное")
    void getAllDishes_Success() {
        // when
        ResponseEntity<DishDTO[]> response = restTemplate.getForEntity(baseUrl(), DishDTO[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DishDTO[] dishes = response.getBody();
        assertThat(dishes).isNotNull().hasSizeGreaterThanOrEqualTo(2);

        DishDTO firstDTO = findDishById(dishes, 1L);
        DishDTO secondDTO = findDishById(dishes, 2L);

        assertDish1(firstDTO);
        assertDish2(secondDTO);
    }


    /**
     * Проверяет получение блюда по ID.
     * given: в БД есть блюдо с ID = 101.
     * when: выполняется GET на {@code /dishes/101}.
     * then: HTTP 200 и корректный DTO блюда.
     */
    @Test
    @DisplayName("Получение блюда по ID - успешное")
    void getDishById_Success() {
        // when
        ResponseEntity<DishDTO> response = restTemplate.getForEntity(baseUrl() + "/1", DishDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DishDTO dishDTO = response.getBody();
        assertThat(dishDTO).isNotNull();
        assertThat(dishDTO.getDishId()).isEqualTo(1L);
        assertThat(dishDTO.getShortName()).isEqualTo("Паста");
        assertThat(dishDTO.getBalance()).isEqualTo(100);
        assertThat(dishDTO.getDishComposition()).isEqualTo("Макароны, соус, сыр");
    }


    /**
     * Находит блюдо с указанным ID в массиве блюд.
     *
     * @param dishDTOS массив DTO блюд
     * @param id  идентификатор блюда
     * @return найденный {@link DishDTO}
     * @throws AssertionError если блюдо не найдено
     */
    private DishDTO findDishById(DishDTO[] dishDTOS, Long id) {
        return List.of(dishDTOS).stream()
                .filter(dishDTO -> id.equals(dishDTO.getDishId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Блюдо с id=" + id + " не найдено"));
    }


    /**
     * Проверяет содержимое блюда с ID = 101 (Паста).
     *
     * @param dishDTO DTO блюда
     */
    private void assertDish1(DishDTO dishDTO) {
        assertThat(dishDTO.getDishId()).isEqualTo(1L);
        assertThat(dishDTO.getShortName()).isEqualTo("Паста");
        assertThat(dishDTO.getBalance()).isEqualTo(100);
        assertThat(dishDTO.getDishComposition()).isEqualTo("Макароны, соус, сыр");
    }


    /**
     * Проверяет содержимое блюда с ID = 102 (Салат).
     *
     * @param dishDTO DTO блюда
     */
    private void assertDish2(DishDTO dishDTO) {
        assertThat(dishDTO.getDishId()).isEqualTo(2L);
        assertThat(dishDTO.getShortName()).isEqualTo("Салат");
        assertThat(dishDTO.getBalance()).isEqualTo(80);
        assertThat(dishDTO.getDishComposition()).isEqualTo("Овощи, масло, зелень");
    }
}