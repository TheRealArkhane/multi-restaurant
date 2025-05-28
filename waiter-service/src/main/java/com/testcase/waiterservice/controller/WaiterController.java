package com.testcase.waiterservice.controller;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.commondto.waiterservice.WaiterDTO;
import com.testcase.waiterservice.service.impl.WaiterServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/waiters")
@RequiredArgsConstructor
@Tag(name = "Официанты", description = "Операции для управления данными официантов")
public class WaiterController {

    private final WaiterServiceImpl waiterServiceImpl;

    @Operation(summary = "Получить всех официантов",
            description = "Возвращает список всех официантов.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список официантов успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WaiterDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping
    public List<WaiterDTO> getAllWaiters() {
        return waiterServiceImpl.getAllWaiters();
    }


    @Operation(summary = "Получить официанта по ID",
            description = "Возвращает официанта по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Официант успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WaiterDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Официант не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping("/{id}")
    public WaiterDTO getWaiterById(@PathVariable Long id) {
        return waiterServiceImpl.getWaiterById(id);
    }


    @Operation(summary = "Получить список официантов постранично",
            description = "Возвращает постраничный список официантов. "
                    + "Параметры: page (номер страницы), size (размер страницы).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Официанты успешно получены",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WaiterDTO.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @GetMapping("/page")
    public Page<WaiterDTO> getWaitersPage(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return waiterServiceImpl.getWaitersPage(page, size);
    }
}
