package com.example.simple_todo.services;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dtos.TodoDto;
import com.example.simple_todo.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoDto> getAll() {
        return todoRepository.findAll().stream().map(Todo::todoToDto).collect(Collectors.toList());
    }

    public TodoDto create(Todo todo) {
        return todoRepository.save(todo).todoToDto();
    }

    public TodoDto update(Todo todo) {
        return todoRepository.save(todo).todoToDto();
    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
