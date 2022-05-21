package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.exception.ImageServiceException;
import com.example.simple_todo.repository.ImageRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String getImageInBase64(Long taskId) {
        ResponseEntity<String> response = imageRepository.getImage(taskId);
        if (response != null && response.getBody() != null)
            return Base64.getEncoder().encodeToString(response.getBody().getBytes(StandardCharsets.UTF_8));

        return null;
    }

    public void storeImageOnServer(Long taskId, MultipartFile imageFile) {
        if (!imageRepository.storeImage(taskId, imageFile))
            throw new ImageServiceException("Cannot store image");
    }

    public void deleteImageFromServer(Long taskId) {
        if (!imageRepository.deleteImage(taskId))
            throw new ImageServiceException("Cannot delete image");
    }

    public void deleteRecursiveImageFromServer(Todo todo) {
        for (Todo t: todo.getSubtasks())
            if (t.getSubtasks() != null)
                deleteRecursiveImageFromServer(t);

        if (!imageRepository.deleteImage(todo.getId()))
            throw new ImageServiceException("Bad response from image server");
    }
}
