package com.example.simple_todo.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TodoUpdateDto {
    @NotNull(message = "Id is mandatory")
    @Min(value = 1, message = "Todo id must be greater than 0")
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Done is mandatory")
    private Boolean done;
}
