package com.example.simple_todo.controller;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.service.TodoService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> getAll(@RequestHeader (name = "Authorization") String authStr) {
        return todoService.getAllTodoByUserId(authStr);
    }

    @PostMapping
    public Todo create(@RequestHeader (name = "Authorization") String authStr,
                       @Valid @RequestBody TodoCreateDto todoCreate) {
        return todoService.create(authStr, todoCreate);
    }

    @PutMapping
    public Todo update(@RequestHeader (name = "Authorization") String authStr,
                       @Valid @RequestBody TodoUpdateDto todo) {
        return todoService.update(authStr, todo);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@RequestHeader (name = "Authorization") String authStr,
                           @PathVariable("id") Long id) {
        todoService.delete(authStr, id);
    }
}