package com.example.simple_todo.converter;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dto.TodoReadDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoMapper {
    TodoReadDto todoToTodoReadDto(Todo todo);

    TodoUpdateDto todoToTodoUpdateDto(Todo todo);
}
