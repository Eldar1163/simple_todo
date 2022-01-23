package com.example.simple_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TodoUpdateDto {
    @NotNull(message = "Id is mandatory")
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Done is mandatory")
    private Boolean done;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
