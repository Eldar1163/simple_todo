package com.example.simple_todo.controller;

import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.ErrorDto;
import com.example.simple_todo.dto.InfoDto;
import com.example.simple_todo.request.AuthRequest;
import com.example.simple_todo.request.RegistrationRequest;
import com.example.simple_todo.jwt_util.JwtTokenUtil;
import com.example.simple_todo.response.AuthResponse;
import com.example.simple_todo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Object registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        if (userService.isUserExists(registrationRequest.getUsername()))
            return new ErrorDto("User with name " + registrationRequest.getUsername() + " already exists.");

        User user = new User();
        user.setPassword(registrationRequest.getPassword());
        user.setUsername(registrationRequest.getUsername());

        userService.saveUser(user);
        return new InfoDto("Registration complete. Log in to take jwt token.");
    }

    @PostMapping("/api/todo/auth")
    public AuthResponse auth(@RequestBody @Valid AuthRequest request) {
        User user = userService.getUserByUsernameAndPassword(request.getUsername(), request.getPassword());
        String token = jwtTokenUtil.generateToken(user.getId());
        return new AuthResponse(token);
    }
}
