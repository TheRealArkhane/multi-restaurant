package com.testcase.kitchenservice.controller.dish;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.kitchenservice.dto.DishDTO;
import com.testcase.kitchenservice.service.impl.DishServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
@Tag(name = "Блюда", description = "Операции для управления блюдами")
public class DishController {

    private final DishServiceImpl dishServiceImpl;

    @Operation(summary = "Получить все блюда",
            description = "Возвращает список всех блюд.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список блюд успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DishDTO.class))
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
    @GetMapping
    public List<DishDTO> getAllDishes() {
        return dishServiceImpl.getAllDishes();
    }


    @Operation(summary = "Получить блюдо по ID",
            description = "Возвращает информацию о блюде по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блюдо успешно получено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DishDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Блюдо не найдено",
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
    @GetMapping("/{id}")
    public DishDTO getDishById(@PathVariable Long id) {
        return dishServiceImpl.getDishDTOById(id);
    }
}
