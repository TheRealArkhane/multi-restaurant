package com.testcase.commondto.waiterservice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для официанта")
public class WaiterDTO {

    @Schema(description = "ID официанта", example = "1")
    private Long id;

    @Schema(description = "Имя официанта", example = "Виктор")
    private String name;

    @Schema(description = "Дата и время приема на работу", example = "2025-04-06T13:44:58.760Z")
    private OffsetDateTime employmentDate;

    @Schema(description = "Пол официанта", example = "MALE")
    private Sex sex;
}
