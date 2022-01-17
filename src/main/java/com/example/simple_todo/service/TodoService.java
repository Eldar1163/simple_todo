package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.TodoCreateDto;
import com.example.simple_todo.dto.TodoUpdateDto;
import com.example.simple_todo.jwt_util.JwtTokenUtil;
import com.example.simple_todo.repository.TodoRepository;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    public TodoService(TodoRepository todoRepository,
                       UserRepository userRepository,
                       JwtTokenUtil jwtTokenUtil) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public List<Todo> getAllTodoByUserId(String authStr) {
        Long id = jwtTokenUtil.getUserIdFromAuthHeader(authStr);
        return todoRepository.findAllByUserId(id);
    }

    public Todo create(String authStr, TodoCreateDto todoCreate) {
        User user = userRepository.getById(jwtTokenUtil.getUserIdFromAuthHeader(authStr));
        LocalDateTime currentDateTime = LocalDateTime.now();
        Todo todo = new Todo(
                user,
                todoCreate.getTitle(),
                false,
                currentDateTime,
                currentDateTime);
        return todoRepository.save(todo);
    }

    public Todo update(String authStr, TodoUpdateDto todoUpdate) {
        Todo todo = todoRepository.findById(todoUpdate.getId()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cannot found todo with id = " + todoUpdate.getId()));
        Long userId = jwtTokenUtil.getUserIdFromAuthHeader(authStr);
        if (userId.equals(todo.getUser().getId())) {
            todo.setTitle(todoUpdate.getTitle());
            todo.setDone(todoUpdate.getDone());
            todo.setUpdatedAt(LocalDateTime.now());
            return todoRepository.save(todo);
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cannot found todo with id = " + todoUpdate.getId());
        }
    }

    public void delete(String authStr, Long id) {
        Long userId = jwtTokenUtil.getUserIdFromAuthHeader(authStr);
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found todo with id = " + id));
        if (userId.equals(todo.getUser().getId()))
            todoRepository.delete(todo);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot found todo with id = " + id);
    }
}
