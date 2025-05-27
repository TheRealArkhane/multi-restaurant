package com.testcase.commondto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для передачи сообщения об ошибке")
public class ErrorMessageDTO {

    @Schema(description = "Текст ошибки", example = "An unexpected error occurred")
    private String message;
}
