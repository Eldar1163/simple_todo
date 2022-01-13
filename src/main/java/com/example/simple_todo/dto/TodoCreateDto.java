package com.example.simple_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class TodoCreateDto {
    @NotBlank(message = "Title is mandatory")
    private String title;
}
