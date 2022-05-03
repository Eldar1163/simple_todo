package com.example.simple_todo.service;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.exception.BadImageServerResponseException;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ImageService {
    private final ConfigProperties cofig;
    RestTemplate restTemplate;

    public ImageService(ConfigProperties config) {
        this.cofig = config;

        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return !(response.getStatusCode() == HttpStatus.NOT_FOUND ||
                        response.getStatusCode() == HttpStatus.OK);
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                throw new BadImageServerResponseException("Image server error");
            }
        });

    }

    public String getImageInBase64(Long taskId) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                cofig.getImageServerPath() + "?taskid={taskId}",
                String.class,
                taskId);
        if (
                (response.getStatusCode().is2xxSuccessful() ||
                response.getBody() != null) &&
                !response.getBody().equals("Resource not found")
        )
            return Base64.getEncoder().encodeToString(response.getBody().getBytes(StandardCharsets.UTF_8));
        else
            return null;
    }

    public Boolean storeImageOnServer(Long taskId, MultipartFile imageFile) {
        if (imageFile == null)
            return true;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("image", imageFile.getResource());


        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        cofig.getImageServerPath() + "?taskid={taskId}",
                        requestEntity,
                        String.class,
                        taskId
                );

        return response.getStatusCode().is2xxSuccessful();
    }

    public void deleteRecursiveImageFromServer(Todo todo) {
        for (Todo t: todo.getSubtasks()) {
            deleteRecursiveImageFromServer(t);
            restTemplate.delete(
                    cofig.getImageServerPath() + "?taskid={taskId}",
                    t.getId());
        }

        restTemplate.delete(
                cofig.getImageServerPath() + "?taskid={taskId}",
                todo.getId());
    }
}