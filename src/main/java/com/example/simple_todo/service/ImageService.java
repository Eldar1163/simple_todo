package com.example.simple_todo.service;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.Todo;
import com.example.simple_todo.exception.BadImageServerResponseException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ImageService {
    RestTemplate restTemplate;
    String url;

    public ImageService(ConfigProperties config, RestTemplateBuilder builder) {
        url = config.getImageServerPath() + "?taskid={taskId}";

        restTemplate = builder.build();
    }

    public String getImageInBase64(Long taskId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    url,
                    String.class,
                    taskId);

            if (response.getBody() != null)
                return Base64.getEncoder().encodeToString(response.getBody().getBytes(StandardCharsets.UTF_8));
        } catch (HttpClientErrorException ignore) {

        }

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
                        url,
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
                    url,
                    t.getId());
        }

        restTemplate.delete(
                url,
                todo.getId());
    }
}
