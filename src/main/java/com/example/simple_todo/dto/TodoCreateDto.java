package com.example.simple_todo.dto;

import javax.validation.constraints.NotBlank;

public class TodoCreateDto {
    @NotBlank(message = "Title is mandatory")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
