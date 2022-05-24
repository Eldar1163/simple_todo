package com.example.simple_todo.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TodoWithoutSubtaskDto {
    @NotNull(message = "Id is mandatory")
    @Min(value = 1, message = "Todo id must be greater than 0")
    private Long id;

    private String image;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Done is mandatory")
    private Boolean done;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
