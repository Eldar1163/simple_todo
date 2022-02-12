package com.example.simple_todo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TodoReadDto {
    private Long id;

    private String title;

    private Boolean done;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<TodoReadDto> subtasks;
}
