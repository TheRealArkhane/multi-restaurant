package com.testcase.waiterservice.controller;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.commondto.waiterservice.OrderDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.waiterservice.dto.request.OrderCalculateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderCreateRequestDTO;
import com.testcase.waiterservice.dto.request.OrderFilterDTO;
import com.testcase.waiterservice.dto.PaymentDTO;
import com.testcase.waiterservice.entity.PaymentType;
import com.testcase.waiterservice.service.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Операции для управления заказами")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;


    @Operation(summary = "Получить все заказы",
            description = "Получить список всех заказов официантов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказы успешно получены",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderServiceImpl.getAllOrders();
    }


    @Operation(summary = "Получить постраничный список заказов по заданным фильтрам",
            description = "Фильтрация заказов по статусу, дате, официанту и номеру столика.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Список заказов по фильтру успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/search")
    public Page<OrderDTO> getOrdersByFilter(@RequestBody OrderFilterDTO filter,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return orderServiceImpl.getOrdersByFilter(filter, page, size);
    }


    @Operation(summary = "Получить заказ по ID",
            description = "Получить конкретный заказ официанта по ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        return orderServiceImpl.getOrderById(id);
    }


    @Operation(summary = "Получить статус заказа по ID",
            description = "Получить текущий статус заказа по его ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Статус заказа успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping("/{id}/status")
    public String getOrderStatusByOrderId(@PathVariable Long id) {
        return orderServiceImpl.getOrderStatusByOrderId(id);
    }


    @Operation(summary = "Создать новый заказ",
            description = "Создать новый заказ для выбранного столика от выбранного официанта.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Заказ успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Неверный формат запроса",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Официант не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody @Valid OrderCreateRequestDTO requestDTO) {
        return orderServiceImpl.createOrder(requestDTO);
    }


    @Operation(summary = "Рассчитать позиции заказа",
            description = "Добавить и обновить позиции по переданному количеству для указанного заказа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно пересчитан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Неверный формат запроса или значение позиции",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Заказ или позиция не найдены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/{id}/calculate")
    public OrderDTO calculateOrder(@PathVariable Long id, @RequestBody @Valid OrderCalculateRequestDTO requestDTO) {
        return orderServiceImpl.calculateOrder(id, requestDTO);
    }


    @Operation(summary = "Очистить позиции заказа",
            description = "Удалить все позиции заказа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Позиции заказа успешно удалены"),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @DeleteMapping("/{id}/clear-positions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearPositions(@PathVariable Long id) {
        orderServiceImpl.clearOrderPositions(id);
    }


    @Operation(summary = "Отправить заказ на кухню",
            description = "Отправить заказ в Kafka топик для дальнейшей обработки на кухне.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно отправлен на кухню",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/{id}/send-to-kitchen")
    public OrderDTO sendOrderToKitchen(@PathVariable Long id) {
        return orderServiceImpl.sendOrderToKitchen(id);
    }


    @Operation(summary = "Оплатить заказ",
            description = "Произвести оплату заказа, используя указанный тип оплаты.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно оплачен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка оплаты",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/{id}/pay")
    public PaymentDTO payOrder(@PathVariable Long id, @RequestParam PaymentType paymentType) {
        return orderServiceImpl.payOrder(id, paymentType);
    }


    @Operation(summary = "Обслужить заказ",
            description = "Перевести заказ в состояние 'PAID_AND_SERVED' если заказ подготовлен и оплачен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно обслужен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден или не оплачен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )
            )
    })
    @PostMapping("/{id}/serve")
    public OrderDTO serveOrder(@PathVariable Long id) {
        return orderServiceImpl.serveOrder(id);
    }


    @Operation(summary = "Отменить заказ",
            description = "Отменить заказ с переходом в новый статус. "
                    + "При этом для части статусов обновление производится через Kafka.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ успешно отменён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Неверное состояние заказа для отмены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @PostMapping("/{id}/cancel")
    public OrderDTO cancelOrder(
            @PathVariable Long id,
            @Parameter(
                    description = "Новый статус заказа",
                    schema = @Schema(
                            allowableValues = {
                                    "CANCELLED_BEFORE_SEND",
                                    "CANCELLED_BY_WAITER",
                                    "CANCELLED_WHILE_COOKING_BY_WAITER",
                                    "UNSUCCESSFUL_WAITER_REASON",
                                    "UNSUCCESSFUL_VISITOR_UNPAID",
                                    "UNSUCCESSFUL_VISITOR_REASON"
                            }))
            @RequestParam OrderStatus status) {
        return orderServiceImpl.cancelOrder(id, status);
    }
}
