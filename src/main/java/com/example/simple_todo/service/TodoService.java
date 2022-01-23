package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllWhereParentIsNull() {
        return todoRepository.findAllByParentIsNull();
    }

    public Todo create(TodoCreateDto todoCreate) {
        Todo parent = (todoCreate.getParent() != null) ?
                todoRepository.findById(todoCreate.getParent())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find parent todo")) :
                null;
        LocalDateTime currentDateTime = LocalDateTime.now();
        Todo todo = new Todo(
                parent,
                todoCreate.getTitle(),
                false,
                currentDateTime,
                currentDateTime);
        return todoRepository.save(todo);
    }

    public TodoUpdateDto update(TodoUpdateDto todoUpdate) {
        Todo todo = todoRepository.findById(todoUpdate.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        todo.setTitle(todoUpdate.getTitle());
        todo.setDone(todoUpdate.isDone());
        todo.setUpdatedAt(LocalDateTime.now());
        TodoUpdateDto responseDto = new TodoUpdateDto(
                todo.getId(),
                todo.getTitle(),
                todo.isDone(),
                todo.getCreatedAt(),
                todo.getUpdatedAt());
        todoRepository.save(todo);
        return responseDto;
    }

    public void delete(Long id) {
        todoRepository.delete(todoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found")));
    }
}
