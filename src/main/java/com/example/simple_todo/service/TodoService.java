package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.*;
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

    public List<TodoReadDto> getAll(Authentication auth) {
        Long id = ((UserClaims)auth.getPrincipal()).getId();

        return ObjectMapperUtils.mapAll(
                todoRepository.findAllByUserIdAndParentIsNull(id),
                TodoReadDto.class);
    }

    public TodoReadDto create(Authentication auth, TodoCreateDto todoCreate) {
        Long id = ((UserClaims)auth.getPrincipal()).getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found user, check your token")
        );
        Todo parent = (todoCreate.getParent() != null) ?
                todoRepository.findById(todoCreate.getParent())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find parent todo")) :
                null;
        if (parent != null && !parent.getUser().getId().equals(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create todo with parent id = " + todoCreate.getParent());
        LocalDateTime currentDateTime = LocalDateTime.now();
        Todo todo = new Todo(
                user,
                parent,
                todoCreate.getTitle(),
                false,
                currentDateTime,
                currentDateTime);
        todoRepository.save(todo);
        return ObjectMapperUtils.map(todo, TodoReadDto.class);
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
            TodoUpdateDto responseDto = ObjectMapperUtils.map(todo, TodoUpdateDto.class);
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
