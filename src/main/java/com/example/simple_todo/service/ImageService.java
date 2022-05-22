package com.example.simple_todo.service;

import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.dto.ImageDto;
import com.example.simple_todo.exception.ImageServiceException;
import com.example.simple_todo.repository.ImageRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageDto getImageInBase64(Long taskId) {
        ResponseEntity<ImageDto> response = imageRepository.getImage(taskId);
        if (response != null && response.getBody() != null)
            return response.getBody();

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
