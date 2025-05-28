package com.testcase.kitchenservice.controller.kitchenorder;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.kitchenservice.service.impl.KitchenOrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/kitchen/orders")
@Tag(name = "Internal API", description = "API для внутреннего обмена данными с сервисом официантов через Feign Client")
@RequiredArgsConstructor
@Profile("!grpc")
public class InternalKitchenOrderController {

    private final KitchenOrderServiceImpl kitchenOrderServiceImpl;


    @Operation(
            summary = "Валидация количества блюд",
            description = "Проверяет количество блюд на кухне с количеством в заказе "
                    + "перед отправкой на кухню. Возвращает true, если заказ валиден."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно валидирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные заказа",
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
                            schema = @Schema(implementation = ErrorMessageDTO.class))
            )
    })
    @PostMapping("/validate")
    public Boolean validateOrder(@RequestBody OrderValidationDTO validationDTO) {
        return kitchenOrderServiceImpl.validateOrder(validationDTO);
    }
}
