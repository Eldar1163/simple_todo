package com.example.simple_todo.service;

import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.UserClaims;
import com.example.simple_todo.exception.InvalidPasswordException;
import com.example.simple_todo.exception.NotFoundException;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot found user with id = " + id));
    }

    public Boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void saveUser(String username, String password) {
        String encodedPass = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPass);

        userRepository.save(user);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Cannot found user with username = " + username));
        if (passwordEncoder.matches(password, user.getPassword()))
            return user;
        else
            throw new InvalidPasswordException();
    }

    public static Long getUserIdFromAuth(Authentication auth) {
        return ((UserClaims)auth.getPrincipal()).getId();
    }
}
