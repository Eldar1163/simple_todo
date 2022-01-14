package com.example.simple_todo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoCreateDto {
    @NotBlank(message = "Title is mandatory")
    private String title;
}
