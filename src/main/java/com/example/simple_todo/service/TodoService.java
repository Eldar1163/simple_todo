package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.*;
import com.example.simple_todo.exception.NotFoundException;
import com.example.simple_todo.repository.TodoRepository;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    private final UserRepository userRepository;

    private final TodoMapper todoMapper;

    public TodoService(TodoRepository todoRepository,
                       UserRepository userRepository,
                       TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.todoMapper = todoMapper;
    }

    public List<TodoReadDto> getAll(Long userId) {
        List<Todo> todoList = todoRepository.findAllByUserIdAndParentIsNull(userId);

        return todoList.stream()
                .map(todoMapper::todoToTodoReadDto)
                .collect(Collectors.toList());
    }

    public TodoReadDto create(Long userId, TodoCreateDto todoCreate) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Cannot found user, check your token")
        );
        Todo parent = (todoCreate.getParent() != null) ?
                todoRepository.findById(todoCreate.getParent())
                        .orElseThrow(() -> new NotFoundException("Cannot find parent todo")) :
                null;
        if (parent != null && !parent.getUser().getId().equals(userId))
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
        return todoMapper.todoToTodoReadDto(todo);
    }

    public TodoUpdateDto update(Long userId, TodoUpdateDto todoUpdate) {
        Todo todo = todoRepository.findById(todoUpdate.getId()).orElseThrow(
                () -> new NotFoundException("Cannot found todo with id = " + todoUpdate.getId()));
        if (userId.equals(todo.getUser().getId())) {
            todo.setTitle(todoUpdate.getTitle());
            todo.setDone(todoUpdate.getDone());
            todo.setUpdatedAt(LocalDateTime.now());
            TodoUpdateDto responseDto = todoMapper.todoToTodoUpdateDto(todo);
            todoRepository.save(todo);
            return responseDto;
        }
        else {
            throw new NotFoundException("Cannot found todo with id = " + todoUpdate.getId());
        }
    }

    public void delete(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new NotFoundException("Cannot found todo with id = " + todoId));
        if (userId.equals(todo.getUser().getId()))
            todoRepository.delete(todo);
        else
            throw new NotFoundException("Cannot found todo with id = " + todoId);
    }
}
