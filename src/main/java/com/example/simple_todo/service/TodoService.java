package com.example.simple_todo.service;

import com.example.simple_todo.converter.TodoMapper;
import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.*;
import com.example.simple_todo.exception.ImageServiceException;
import com.example.simple_todo.exception.NotFoundException;
import com.example.simple_todo.repository.TodoRepository;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    private final UserRepository userRepository;

    private final TodoMapper todoMapper;

    private final ImageService imageService;

    private final TransactionTemplate transactionTemplate;

    public TodoService(TodoRepository todoRepository,
                       UserRepository userRepository,
                       TodoMapper todoMapper,
                       ImageService imageService,
                       TransactionTemplate transactionTemplate) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.todoMapper = todoMapper;
        this.imageService = imageService;
        this.transactionTemplate = transactionTemplate;
    }

    public List<TodoReadDto> getAll(Long userId) {
        List<Todo> todoList = todoRepository.findAllByUserIdAndParentIsNull(userId);
        return getTodoListWithImages(todoList);
    }

    public TodoReadDto create(Long userId, TodoCreateDto todoCreate, MultipartFile imageFile) {
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
        todo = todoRepository.save(todo);
        if (imageFile != null && !imageService.storeImageOnServer(todo.getId(), imageFile)) {
            serverDelete(todo.getId());
            throw new ImageServiceException("Cannot save your image, try again later.");
        }
        return todoMapper.todoToTodoReadDto(todo, imageFileToBase64Str(imageFile));
    }

    public TodoWithoutSubtaskDto update(Long userId, TodoWithoutSubtaskDto todoWithoutSubtaskDto, MultipartFile imageFile) {
        Todo todo = todoRepository.findById(todoWithoutSubtaskDto.getId()).orElseThrow(
                () -> new NotFoundException("Cannot found todo with id = " + todoWithoutSubtaskDto.getId()));
        if (userId.equals(todo.getUser().getId())) {
            todo.setTitle(todoWithoutSubtaskDto.getTitle());
            todo.setDone(todoWithoutSubtaskDto.getDone());
            todo.setUpdatedAt(LocalDateTime.now());
            todo = todoRepository.save(todo);
            if (imageFile != null && !imageService.storeImageOnServer(todo.getId(), imageFile)) {
                serverDelete(todo.getId());
                throw new ImageServiceException("Cannot save your image, try again later.");
            }
            return todoMapper.todoToTodoWithoutSubtaskDto(todo, imageFileToBase64Str(imageFile));
        }
        else {
            throw new NotFoundException("Cannot found todo with id = " + todoWithoutSubtaskDto.getId());
        }
    }

    public void delete(Long userId, Long todoId) {
        transactionTemplate.execute(status -> {
            Todo todo = todoRepository.findById(todoId).orElseThrow(
                    () -> new NotFoundException("Cannot found todo with id = " + todoId));
            if (userId.equals(todo.getUser().getId())) {
                todoRepository.delete(todo);
                imageService.deleteRecursiveImageFromServer(todo);
            } else
                throw new NotFoundException("Cannot found todo with id = " + todoId);
            return status;
        });
    }

    public void serverDelete(Long todoId) {
        if (todoRepository.findById(todoId).isPresent())
            todoRepository.delete(todoRepository.findById(todoId).get());
    }

    public String imageFileToBase64Str(MultipartFile imageFile) {
        if (imageFile == null)
            return null;
        try {
            byte[] imageBytes = imageFile.getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException exception) {
            throw new ImageServiceException("Something goes wrong");
        }
    }

    TodoReadDto addImageToTodo(Todo inputTodo) {
        return todoMapper.todoToTodoReadDto(inputTodo, imageService.getImageInBase64(inputTodo.getId()));
    }

    List<TodoReadDto> getTodoListWithImages(List<Todo> inputList) {
        List<TodoReadDto> outputList = new ArrayList<>();
        for (Todo todo: inputList) {
            TodoReadDto outputTodo = addImageToTodo(todo);
            outputTodo.setSubtasks(getTodoListWithImages(todo.getSubtasks()));
            outputList.add(outputTodo);
        }
        return outputList;
    }

}
