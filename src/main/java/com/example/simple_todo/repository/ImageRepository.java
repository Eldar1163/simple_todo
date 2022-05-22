package com.example.simple_todo.repository;

import com.example.simple_todo.config.ImageServerConfig;
import com.example.simple_todo.dto.ImageDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageRepository {
    private final RestTemplate restTemplate;
    private final String url;

    public ImageRepository(ImageServerConfig config, RestTemplateBuilder builder) {
        url = config.getPath() + "?taskid={taskId}";
        restTemplate = builder.build();
    }

    public ResponseEntity<ImageDto> getImage(Long taskId) {
        try {
            return restTemplate.getForEntity(
                    url,
                    ImageDto.class,
                    taskId);
        } catch (HttpClientErrorException ignore) {
            return null;
        }
    }

    public Boolean storeImage(Long taskId, MultipartFile imageFile) {
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

    public Boolean deleteImage(Long id) {
        try {
            restTemplate.delete(
                    url,
                    id);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode() != HttpStatus.NOT_FOUND)
                return false;
        }

        return true;
    }
}
