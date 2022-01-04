package com.example.simple_todo.controllers;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dtos.TodoDto;
import com.example.simple_todo.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoDto> getAll() {
        return todoService.getAll();
    }

    @PostMapping
    public TodoDto create(@RequestBody Todo todo) {
        return todoService.create(todo);
    }

    @PutMapping
    public TodoDto update(@RequestBody Todo todo) {
        return todoService.update(todo);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        todoService.delete(id);
    }
}