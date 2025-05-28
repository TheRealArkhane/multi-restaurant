package com.testcase.kitchenservice.controller.kitchenorder;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.commondto.waiterservice.OrderStatus;
import com.testcase.kitchenservice.dto.CriteriaDTO;
import com.testcase.kitchenservice.dto.KitchenOrderDTO;
import com.testcase.kitchenservice.service.impl.KitchenOrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kitchen")
@RequiredArgsConstructor
@Tag(name = "Заказы кухни", description = "Операции для управления заказами на кухне")
public class KitchenOrderController {

    private final KitchenOrderServiceImpl kitchenOrderServiceImpl;


    @Operation(summary = "Принять заказ в работу",
            description = "Изменить статус заказа на COOKING (Готовится).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно принят",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KitchenOrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный статус заказа"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/orders/{id}/accept")
    public KitchenOrderDTO acceptOrder(@PathVariable Long id) {
        return kitchenOrderServiceImpl.updateOrderStatus(id, OrderStatus.COOKING);
    }


    @Operation(summary = "Отменить заказ",
            description = "Изменить статус заказа на CANCELLED_BY_KITCHEN (Отменен кухней) "
                    + "с возвращением в баланс позиций блюд, или, если заказ уже готовится (COOKING), "
                    + "изменяет статус заказа на CANCELLED_WHILE_COOKING_BY_KITCHEN "
                    + "(Отменен кухней при процессе приготовления) без возвращения количества блюд в баланс.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно отклонен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KitchenOrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный статус заказа"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/orders/{id}/cancel")
    public KitchenOrderDTO declineOrder(@PathVariable Long id) {
        return kitchenOrderServiceImpl.updateOrderStatus(id, OrderStatus.CANCELLED_BY_KITCHEN);
    }


    @Operation(summary = "Завершить заказ",
            description = "Изменить статус заказа на READY (Готов).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно завершен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KitchenOrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный статус заказа"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/orders/{id}/finish")
    public KitchenOrderDTO finishOrder(@PathVariable Long id) {
        return kitchenOrderServiceImpl.updateOrderStatus(id, OrderStatus.READY);
    }


    @Operation(summary = "Поиск заказов по критериям",
            description = "Возвращает список заказов на кухне, соответствующих переданным критериям поиска. "
                    + "Можно передать один или несколько параметров, либо пустой объект для получения всех заказов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заказов успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = KitchenOrderDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/orders/search")
    public List<KitchenOrderDTO> getOrdersByCriteria(@RequestBody CriteriaDTO criteriaDTO) {
        return kitchenOrderServiceImpl.getKitchenOrdersByCriteria(criteriaDTO);
    }


    @Operation(
            summary = "Получить заказ на кухне по ID",
            description = "Возвращает заказ, находящийся на кухне, по его уникальному идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KitchenOrderDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )
            )
    })
    @GetMapping("/orders/{id}")
    public KitchenOrderDTO getKitchenOrderById(@PathVariable Long id) {
        return kitchenOrderServiceImpl.getKitchenOrderById(id);
    }
}
