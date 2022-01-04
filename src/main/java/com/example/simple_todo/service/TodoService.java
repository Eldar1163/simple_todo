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

    public List<Todo> getAll() {
        return todoRepository.findAll();
    }

    public Todo create(TodoCreateDto todoCreate) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Todo todo = new Todo(todoCreate.getTitle(),
                false,
                currentDateTime,
                currentDateTime);
        return todoRepository.save(todo);
    }

    public Todo update(TodoUpdateDto todoUpdate) {
        Todo todo = todoRepository.findById(todoUpdate.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        todo.setTitle(todoUpdate.getTitle());
        todo.setDone(todoUpdate.isDone());
        todo.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
