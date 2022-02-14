package com.example.simple_todo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserClaims {
    private Long id;
    private String username;
}
