package com.testcase.waiterservice.controller;

import com.testcase.commondto.ErrorMessageDTO;
import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.waiterservice.service.impl.MenuServiceImpl;
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
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Меню", description = "Операции для управления позициями меню")
public class MenuController {

    private final MenuServiceImpl menuServiceImpl;

    @Operation(summary = "Получить все меню",
            description = "Возвращает список всех позиций меню.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список меню успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDTO.class)
                    ))
    })
    @GetMapping
    public List<MenuDTO> getAllMenus() {
        return menuServiceImpl.getAllMenus();
    }


    @Operation(summary = "Получить позицию меню по ID",
            description = "Возвращает позицию меню по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Позиция меню успешно получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Позиция меню не найдена",
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
    public MenuDTO getMenuById(@PathVariable Long id) {
        return menuServiceImpl.getMenuById(id);
    }


    @Operation(summary = "Получить позиции меню постранично",
            description = "Возвращает позиции меню на нужной странице нужного размера. "
                    + "Параметры: page (номер страницы), size (размер страницы).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Страница позиций меню успешно получена",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuDTO.class))
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
    @GetMapping("/page")
    public Page<MenuDTO> getMenusPage(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return menuServiceImpl.getMenusPage(page, size);
    }
}
