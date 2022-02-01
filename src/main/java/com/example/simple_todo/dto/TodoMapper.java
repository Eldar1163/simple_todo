package com.example.simple_todo.dto;

import com.example.simple_todo.domain.Todo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoMapper {
    TodoReadDto todoToTodoReadDto(Todo todo);

    TodoUpdateDto todoToTodoUpdateDto(Todo todo);
}
