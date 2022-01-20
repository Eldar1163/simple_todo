package com.example.simple_todo.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class TodoCreateDto {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Min(value = 1)
    private Long parent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }
}
