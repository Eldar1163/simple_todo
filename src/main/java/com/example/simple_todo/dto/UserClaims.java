package com.example.simple_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserClaims {
    private Long id;
    private String username;
}
