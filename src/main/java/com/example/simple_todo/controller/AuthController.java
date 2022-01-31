package com.example.simple_todo.controller;

import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.AuthRequestDto;
import com.example.simple_todo.dto.RegistrationRequestDto;
import com.example.simple_todo.service.JwtTokenUtil;
import com.example.simple_todo.dto.AuthResponseDto;
import com.example.simple_todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class AuthController {

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/api/todo/register")
    public Object registerUser(@RequestBody @Valid RegistrationRequestDto registrationRequestDto) {
        if (userService.isUserExists(registrationRequestDto.getUsername()))
            throw new ResponseStatusException(
                  HttpStatus.CONFLICT,
                  "User with name " + registrationRequestDto.getUsername() + " already exists.");

        userService.saveUser(registrationRequestDto.getUsername(), registrationRequestDto.getPassword());
        return null;
    }

    @PostMapping("/api/todo/auth")
    public AuthResponseDto auth(@RequestBody @Valid AuthRequestDto request) {
        User user = userService.getUserByUsernameAndPassword(request.getUsername(), request.getPassword());
        String token = jwtTokenUtil.generateToken(user.getId());
        return new AuthResponseDto(token);
    }
}
