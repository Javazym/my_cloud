package org.example.agentserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiProxyService {

    private final RestTemplate restTemplate;

    @Value("${python.api.base-url:http://localhost:8000}")
    private String pythonBaseUrl;

    public Map<String, Object> get(String path) {
        String url = pythonBaseUrl + path;
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public Map<String, Object> post(String path, Object requestBody) {
        String url = pythonBaseUrl + path;
        HttpEntity<Object> entity = new HttpEntity<>(requestBody);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}
