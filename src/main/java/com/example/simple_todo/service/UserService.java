package com.example.simple_todo.service;

import com.example.simple_todo.domain.User;
import com.example.simple_todo.exception.InvalidPasswordException;
import com.example.simple_todo.exception.UserNotFoundException;
import com.example.simple_todo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public Boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void saveUser(String username, String password) {
        String encodedPass = bCryptPasswordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPass);

        userRepository.save(user);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if (bCryptPasswordEncoder.matches(password, user.getPassword()))
            return user;
        else
            throw new InvalidPasswordException();
    }
}
