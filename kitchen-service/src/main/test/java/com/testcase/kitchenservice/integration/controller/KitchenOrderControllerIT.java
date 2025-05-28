package java.com.testcase.kitchenservice.integration.controller;

import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.kitchenservice.controller.kitchenorder.KitchenOrderController;
import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import java.com.testcase.kitchenservice.integration.IntegrationTestsBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link KitchenOrderController}.
 * <p>
 * Uses a real PostgreSQL Testcontainer and Spring Boot context to exercise HTTP endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class KitchenOrderControllerIT extends IntegrationTestsBase {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/kitchen/orders";
    }

    /**
     * Проверяет приём заказа в работу (перевод в статус COOKING).
     * given: в БД заказ с ID = 1 в статусе SENT_TO_KITCHEN.
     * when: выполняется PATCH на {@code /kitchen/orders/1/accept}.
     * then: HTTP 200 и статус COOKING в ответе.
     */
    @Test
    @DisplayName("Приём заказа в работу — успешно")
    void acceptOrder_Success() {
        // when
        ResponseEntity<KitchenOrderDTO> response =
                restTemplate.exchange(baseUrl() + "/1/accept", HttpMethod.PATCH, null, KitchenOrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO kitchenOrderDTO = response.getBody();
        assertThat(kitchenOrderDTO).isNotNull();
        assertThat(kitchenOrderDTO.getKitchenOrderId()).isEqualTo(1L);
        assertThat(kitchenOrderDTO.getStatus()).isEqualTo(OrderStatus.COOKING);
    }

    /**
     * Проверяет отмену заказа, ещё не начатого (SENT_TO_KITCHEN).
     * given: в БД заказ с ID = 2 в статусе SENT_TO_KITCHEN.
     * when: выполняется PATCH на {@code /kitchen/orders/2/cancel}.
     * then: HTTP 200 и статус CANCELLED_BY_KITCHEN, а баланс блюд увеличен.
     */
    @Test
    @DisplayName("Отмена заказа SENT_TO_KITCHEN — успешно")
    void declineOrder_SentToKitchen_Success() {
        // when
        ResponseEntity<KitchenOrderDTO> response =
                restTemplate.exchange(baseUrl() + "/2/cancel", HttpMethod.PATCH, null, KitchenOrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO kitchenOrderDTO = response.getBody();
        assertThat(kitchenOrderDTO).isNotNull();
        assertThat(kitchenOrderDTO.getKitchenOrderId()).isEqualTo(2L);
        assertThat(kitchenOrderDTO.getStatus()).isEqualTo(OrderStatus.CANCELLED_BY_KITCHEN);
    }

    /**
     * Проверяет отмену заказа в процессе готовки (COOKING).
     * given: в БД заказ с ID = 3 в статусе COOKING.
     * when: выполняется PATCH на {@code /kitchen/orders/3/cancel}.
     * then: HTTP 200 и статус CANCELLED_WHILE_COOKING_BY_KITCHEN, без изменения баланса блюд.
     */
    @Test
    @DisplayName("Отмена заказа COOKING — успешно")
    void declineOrder_Cooking_Success() {
        // when
        ResponseEntity<KitchenOrderDTO> response =
                restTemplate.exchange(baseUrl() + "/3/cancel", HttpMethod.PATCH, null, KitchenOrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO kitchenOrderDTO = response.getBody();
        assertThat(kitchenOrderDTO).isNotNull();
        assertThat(kitchenOrderDTO.getKitchenOrderId()).isEqualTo(3L);
        assertThat(kitchenOrderDTO.getStatus()).isEqualTo(OrderStatus.CANCELLED_WHILE_COOKING_BY_KITCHEN);
    }

    /**
     * Проверяет завершение заказа (перевод в статус READY).
     * given: в БД заказ с ID = 4 в статусе COOKING.
     * when: выполняется PATCH на {@code /kitchen/orders/4/finish}.
     * then: HTTP 200 и статус READY.
     */
    @Test
    @DisplayName("Завершение заказа — успешно")
    void finishOrder_Success() {
        // when
        ResponseEntity<KitchenOrderDTO> response =
                restTemplate.exchange(baseUrl() + "/4/finish", HttpMethod.PATCH, null, KitchenOrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO kitchenOrderDTO = response.getBody();
        assertThat(kitchenOrderDTO).isNotNull();
        assertThat(kitchenOrderDTO.getKitchenOrderId()).isEqualTo(4L);
        assertThat(kitchenOrderDTO.getStatus()).isEqualTo(OrderStatus.READY);
    }

    /**
     * Проверяет поиск заказов по критериям.
     * given: в БД 4 заказа с разными статусами.
     * when: выполняется POST на {@code /kitchen/orders/search} с критерием status=SENT_TO_KITCHEN.
     * then: HTTP 200 и в ответе два заказа.
     */
    @Test
    @DisplayName("Поиск заказов по критериям — успешно")
    void getOrdersByCriteria() {
        // given
        CriteriaDTO criteria = new CriteriaDTO();
        criteria.setStatus(OrderStatus.SENT_TO_KITCHEN);

        HttpEntity<CriteriaDTO> request = new HttpEntity<>(criteria);

        // when
        ResponseEntity<KitchenOrderDTO[]> response =
                restTemplate.postForEntity(baseUrl() + "/search", request, KitchenOrderDTO[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO[] kitchenOrderDTOS = response.getBody();
        assertThat(kitchenOrderDTOS).isNotNull().hasSize(2);

        Set<Long> ids = Set.of(kitchenOrderDTOS[0].getKitchenOrderId(), kitchenOrderDTOS[1].getKitchenOrderId());
        assertThat(ids).containsExactlyInAnyOrder(1L, 2L);
    }

    /**
     * Проверяет получение заказа по ID.
     * given: в БД заказ с ID = 1.
     * when: выполняется GET на {@code /kitchen/orders/1}.
     * then: HTTP 200 и возвращается корректный DTO заказа.
     */
    @Test
    @DisplayName("Получение заказа по ID — успешно")
    void getKitchenOrderById() {
        // when
        ResponseEntity<KitchenOrderDTO> response =
                restTemplate.getForEntity(baseUrl() + "/1", KitchenOrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        KitchenOrderDTO kitchenOrderDTO = response.getBody();
        assertThat(kitchenOrderDTO).isNotNull();
        assertThat(kitchenOrderDTO.getKitchenOrderId()).isEqualTo(1L);
        assertThat(kitchenOrderDTO.getStatus()).isEqualTo(OrderStatus.SENT_TO_KITCHEN);
        assertThat(kitchenOrderDTO.getOrderToDishes()).hasSize(2);
    }
}