package com.example.simple_todo.controller;

import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoReadDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.service.TodoService;
import com.example.simple_todo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/todo")
@Validated
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoReadDto> getAll(Authentication auth) {
        return todoService.getAll(UserService.getUserIdFromAuth(auth));
    }

    @PostMapping
    public TodoReadDto create(
            Authentication auth,
            @Valid @RequestBody TodoCreateDto todoCreate) {
        return todoService.create(UserService.getUserIdFromAuth(auth), todoCreate);
    }

    @PutMapping
    public TodoUpdateDto update(
            Authentication auth,
            @Valid @RequestBody TodoUpdateDto todo) {
        return todoService.update(UserService.getUserIdFromAuth(auth), todo);
    }

    @DeleteMapping(value = "{id}")
    public void deleteById(
            Authentication auth,
            @PathVariable @Min(value = 1) Long id) {
        todoService.delete(UserService.getUserIdFromAuth(auth), id);
    }
}