package com.example.simple_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {
    Long taskId;
    String imageBase64;
}
