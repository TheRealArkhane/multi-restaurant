package com.testcase.waiterservice.integration.controller;

import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.waiterservice.RunWaiterService;
import com.testcase.waiterservice.dto.request.OrderCalculateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.PaymentType;
import com.testcase.waiterservice.client.impl.FeignValidationClient;
import com.testcase.waiterservice.integration.IntegrationTestsBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты контроллера заказов {@link com.testcase.waiterservice.controller.OrderController}.
 * Проверяет все публичные эндпоинты: получение всех заказов, получения заказа по ID, получения статуса,
 * создание, пересчет позиций, очистку позиций, отправку на кухню, оплату, подачу и отмену заказа.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RunWaiterService.class)
@ActiveProfiles("test")
@Testcontainers
class OrderControllerIT extends IntegrationTestsBase {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @MockitoBean
    private FeignValidationClient feignValidationClient;

    private String baseUrl() {
        return "http://localhost:" + port + "/orders";
    }


    /**
     * Проверяет получение списка всех заказов.
     * given: в тестовой БД 7 заказов в разных статусах.
     * when: выполняется GET-запрос на {@code /orders}.
     * then: HTTP 200 и массив из 7 элементов.
     */
    @Test
    @DisplayName("Получение всех заказов")
    void getAllOrders_Success() {
        // when
        ResponseEntity<OrderDTO[]> response = restTemplate.getForEntity(baseUrl(), OrderDTO[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderDTO[] orders = response.getBody();
        assertThat(orders).isNotNull().hasSize(7);  // по вашему скрипту в БД 7 заказов
    }


    /**
     * Проверяет получение конкретного заказа по ID.
     * given: в тестовой БД заказ с ID = 3, статус PREPARING, 2 позиций.
     * when: выполняется GET-запрос на {@code /orders/3}.
     * then: HTTP 200 и DTO с ожидаемыми полями и размерами списка позиций.
     */
    @Test
    @DisplayName("Получение заказа по ID")
    void getOrderById_Success() {
        // when
        ResponseEntity<OrderDTO> response = restTemplate.getForEntity(baseUrl() + "/3", OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderDTO orderDTO = response.getBody();
        assertThat(orderDTO).isNotNull();
        assertThat(orderDTO.getId()).isEqualTo(3L);
        assertThat(orderDTO.getStatus()).isEqualTo(OrderStatus.PREPARING);
        assertThat(orderDTO.getTableNumber()).isEqualTo("C3");
        assertThat(orderDTO.getOrderPositions()).hasSize(2);
    }


    /**
     * Проверяет получение строкового статуса заказа по ID.
     * given: в тестовой БД заказ с ID = 6, статус SENT_TO_KITCHEN.
     * when: выполняется GET-запрос на {@code /orders/6/status}.
     * then: HTTP 200 и строка "SENT_TO_KITCHEN".
     */
    @Test
    @DisplayName("Получение статуса заказа по ID")
    void getOrderStatusByOrderId_Success() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/6/status", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(OrderStatus.SENT_TO_KITCHEN.name());
    }


    /**
     * Проверяет создание нового заказа.
     * given: DTO с waiterId=1 и tableNumber="A12".
     * when: выполняется POST на {@code /orders/create}.
     * then: HTTP 201 и DTO с валидным ID, статусом PREPARING, waiterId=1 и tableNumber="A12".
     */
    @Test
    @DisplayName("Создание нового заказа")
    void createOrder_Success() {
        // given
        OrderCreateRequestDTO createRequestDTO = new OrderCreateRequestDTO(1L, "A12");

        // when
        ResponseEntity<OrderDTO> response =
                restTemplate.postForEntity(baseUrl() + "/create", createRequestDTO, OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        OrderDTO orderDTO = response.getBody();
        assertThat(orderDTO).isNotNull();
        assertThat(orderDTO.getId()).isPositive();
        assertThat(orderDTO.getWaiter().getId()).isEqualTo(1L);
        assertThat(orderDTO.getTableNumber()).isEqualTo("A12");
        assertThat(orderDTO.getStatus()).isEqualTo(OrderStatus.PREPARING);
    }


    /**
     * Проверяет подсчет позиций заказа.
     * given: только что созданный заказ, затем добавляется блюдо id=1, quantity=1.
     * when: выполняется POST на {@code /orders/{id}/calculate}.
     * then: HTTP 200 и в DTO ровно одна позиция с quantity=1.
     */
    @Test
    @DisplayName("Пересчет позиций заказа")
    void calculateOrder_Success() {
        // given
        OrderCreateRequestDTO createRequestDTO = new OrderCreateRequestDTO(2L, "Z9");

        // when
        OrderDTO createdOrderDTO = restTemplate.postForEntity(
                baseUrl() + "/create", createRequestDTO, OrderDTO.class).getBody();
        Long newId = createdOrderDTO.getId();

        // теперь рассчитываем: добавляем блюдо id=1, qty=1
        OrderCalculateRequestDTO calculateRequestDTO = new OrderCalculateRequestDTO(1L, 1);
        ResponseEntity<OrderDTO> response =
                restTemplate.postForEntity(
                        baseUrl() + "/" + newId + "/calculate", calculateRequestDTO, OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderDTO orderDTO = response.getBody();
        assertThat(orderDTO).isNotNull();
        assertThat(orderDTO.getOrderPositions()).hasSize(1);
        assertThat(orderDTO.getOrderPositions().iterator().next().getQuantity()).isEqualTo(1);
    }


    /**
     * Проверяет удаление всех позиций заказа.
     * given: в БД заказ с ID = 3 со статусом PREPARING и двумя позициями.
     * when: выполняется DELETE на {@code /orders/3/clear-positions}.
     * then: HTTP 204, а при повторном GET позиции отсутствуют.
     */
    @Test
    @DisplayName("Очистка позиций заказа")
    void clearPositions_Success() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Void> req = new HttpEntity<>(httpHeaders);

        // when
        ResponseEntity<Void> response =
                restTemplate.exchange(baseUrl() + "/3/clear-positions", HttpMethod.DELETE, req, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // проверим, что позиции убрались
        OrderDTO orderDTO = restTemplate.getForEntity(baseUrl() + "/3", OrderDTO.class).getBody();
        assertThat(orderDTO.getOrderPositions()).isEmpty();
    }


    /**
     * Проверяет отправку заказа на кухню.
     * given: создан заказ с одной позицией, мок FeignClient отдаёт true.
     * when: выполняется POST на {@code /orders/{id}/send-to-kitchen}.
     * then: HTTP 200 и статус SENT_TO_KITCHEN.
     */
    @Test
    @DisplayName("Отправка заказа на кухню")
    void sendOrderToKitchen_Success() {
        // given
        OrderCreateRequestDTO createRequestDTO = new OrderCreateRequestDTO(1L, "T1");
        OrderDTO createdOrderDTO = restTemplate.postForEntity(
                baseUrl() + "/create", createRequestDTO, OrderDTO.class).getBody();

        // теперь рассчитываем: добавляем блюдо id=1, qty=1
        OrderCalculateRequestDTO calculateRequestDTO = new OrderCalculateRequestDTO(1L, 1);
        createdOrderDTO = restTemplate.postForEntity(
                baseUrl() + "/" + createdOrderDTO.getId() + "/calculate", calculateRequestDTO, OrderDTO.class).getBody();

        when(feignValidationClient.validateOrder(any(OrderValidationDTO.class))).thenReturn(true);

        // when
        ResponseEntity<OrderDTO> response =
                restTemplate.postForEntity(baseUrl() + "/" + createdOrderDTO.getId() + "/send-to-kitchen", null, OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.SENT_TO_KITCHEN);
    }


    /**
     * Проверяет оплату заказа.
     * given: в БД заказ с ID = 4, сумма позиций = 32.25.
     * when: выполняется POST на {@code /orders/4/pay?paymentType=CARD}.
     * then: HTTP 200 и {@link PaymentDTO} с верными полями.
     */
    @Test
    @DisplayName("Оплата заказа")
    void payOrder_Success() {
        // when
        ResponseEntity<PaymentDTO> response =
                restTemplate.postForEntity(
                        baseUrl() + "/4/pay?paymentType=CARD", null, PaymentDTO.class);


        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaymentDTO paymentDTO = response.getBody();
        assertThat(paymentDTO).isNotNull();
        assertThat(paymentDTO.getOrderId()).isEqualTo(4L);
        assertThat(paymentDTO.getPaymentType()).isEqualTo(PaymentType.CARD);
        assertThat(paymentDTO.getPaymentSum()).isEqualTo(32.25);
    }

    /**
     * Проверяет подачу заказа.
     * given: в БД заказ с ID = 5 уже оплачен.
     * when: выполняется POST на {@code /orders/5/serve}.
     * then: HTTP 200 и статус PAID_AND_SERVED.
     */
    @Test
    @DisplayName("Подача заказа")
    void serveOrder_Success() {
        // when
        // заказ 5 уже оплачен
        ResponseEntity<OrderDTO> response =
                restTemplate.postForEntity(baseUrl() + "/5/serve", null, OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.PAID_AND_SERVED);
    }


    /**
     * Проверяет отмену заказа.
     * given: в БД заказ с ID = 7 в статусе COOKING.
     * when: выполняется POST на {@code /orders/7/cancel?status=CANCELLED_WHILE_COOKING_BY_WAITER}.
     * then: HTTP 200 и статус CANCELLED_WHILE_COOKING_BY_WAITER.
     */
    @Test
    @DisplayName("Отмена заказа")
    void cancelOrder_Success() {
        // when
        // заказ 7 в COOKING, отменяем как CANCELLED_WHILE_COOKING_BY_WAITER
        ResponseEntity<OrderDTO> response =
                restTemplate.postForEntity(
                        baseUrl() + "/7/cancel?status=CANCELLED_WHILE_COOKING_BY_WAITER",
                        null,
                        OrderDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus())
                .isEqualTo(OrderStatus.CANCELLED_WHILE_COOKING_BY_WAITER);
    }
}