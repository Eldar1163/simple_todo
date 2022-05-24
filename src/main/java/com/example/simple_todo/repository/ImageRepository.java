package com.example.simple_todo.repository;

import com.example.simple_todo.client.RestTemplateFactory;
import com.example.simple_todo.config.ImageServerConfig;
import com.example.simple_todo.dto.ImageDto;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
public class ImageRepository {
    private final RestTemplate restTemplate;
    private final String url;
    private final String baseUrl;

    public ImageRepository(ImageServerConfig config, RestTemplateFactory restTemplateFactory) {
        baseUrl = config.getPath();
        url = baseUrl + "?taskid={taskId}";
        restTemplate = restTemplateFactory.getObject();
    }

    public ResponseEntity<ImageDto[]> getListOfImages(List<Long> taskIds) {
        String listUrl = baseUrl + "/list";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("idlist", taskIds);


        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        try {
            return restTemplate
                    .postForEntity(
                            listUrl,
                            requestEntity,
                            ImageDto[].class
                    );
        } catch (HttpClientErrorException exception) {
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
