package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.dto.UserClaims;
import com.example.simple_todo.repository.TodoRepository;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository,
                       UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<Todo> getAll(Authentication auth) {
        Long id = ((UserClaims)auth.getPrincipal()).getId();
        return todoRepository.findAllByUserIdAndParentIsNull(id);
    }

    public Todo create(Authentication auth, TodoCreateDto todoCreate) {
        Long id = ((UserClaims)auth.getPrincipal()).getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found user, check your token")
        );
        Todo parent = (todoCreate.getParent() != null) ?
                todoRepository.findById(todoCreate.getParent())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find parent todo")) :
                null;
        LocalDateTime currentDateTime = LocalDateTime.now();
        Todo todo = new Todo(
                user,
                parent,
                todoCreate.getTitle(),
                false,
                currentDateTime,
                currentDateTime);
        return todoRepository.save(todo);
    }

    public TodoUpdateDto update(Authentication auth, TodoUpdateDto todoUpdate) {
        Todo todo = todoRepository.findById(todoUpdate.getId()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cannot found todo with id = " + todoUpdate.getId()));
        Long userId = ((UserClaims)auth.getPrincipal()).getId();
        if (userId.equals(todo.getUser().getId())) {
            todo.setTitle(todoUpdate.getTitle());
            todo.setDone(todoUpdate.getDone());
            todo.setUpdatedAt(LocalDateTime.now());
            TodoUpdateDto responseDto = new TodoUpdateDto(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getDone(),
                    todo.getCreatedAt(),
                    todo.getUpdatedAt());
            todoRepository.save(todo);
            return responseDto;
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cannot found todo with id = " + todoUpdate.getId());
        }
    }

    public void delete(Authentication auth, Long id) {
        Long userId = ((UserClaims)auth.getPrincipal()).getId();
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found todo with id = " + id));
        if (userId.equals(todo.getUser().getId()))
            todoRepository.delete(todo);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found todo with id = " + id);
    }
}
