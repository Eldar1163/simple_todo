package com.example.simple_todo.controller;

import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoReadDto;
import com.example.simple_todo.dto.TodoWithoutSubtaskDto;
import com.example.simple_todo.exception.BadImageFileException;
import com.example.simple_todo.service.TodoService;
import com.example.simple_todo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestPart(name = "metadata")
            @Valid TodoCreateDto todoCreate,
            @RequestPart(name = "image", required = false)
                    MultipartFile imageFile) {
        checkImage(imageFile);
        return todoService.create(UserService.getUserIdFromAuth(auth), todoCreate, imageFile);
    }

    @PutMapping
    public TodoWithoutSubtaskDto update(
            Authentication auth,
            @RequestPart(name = "metadata")
            @Valid TodoWithoutSubtaskDto todo,
            @RequestPart(value = "image", required = false)
                    MultipartFile imageFile) {
        checkImage(imageFile);
        return todoService.update(UserService.getUserIdFromAuth(auth), todo, imageFile);
    }

    @DeleteMapping(value = "{id}")
    public void deleteById(
            Authentication auth,
            @PathVariable @Min(value = 1) Long id) {
        todoService.delete(UserService.getUserIdFromAuth(auth), id);
    }

    private void checkImage(MultipartFile file) {
        if (file == null)
            return;
        if (file.isEmpty())
            throw new BadImageFileException("Your file is empty");
        if (!isSupportedContentType(file.getContentType()))
            throw new BadImageFileException("This file type is unsupported");
    }

    private boolean isSupportedContentType(String contentType) {
        List<String> allowedContentTypes = List.of(
                "image/bmp",
                "image/gif",
                "image/png",
                "image/jpg",
                "image/jpeg");

        return allowedContentTypes.contains(contentType);
    }
}