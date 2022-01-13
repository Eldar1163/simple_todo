package com.example.simple_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserJwtDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -1940135160462007488L;

    private Long id;
    private String username;
}
