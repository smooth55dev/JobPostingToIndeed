package com.example.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.example.demo.dto.GraphQLRequest;


@Service
public class GraphQLService {
    private final RestTemplate restTemplate;

    public GraphQLService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String postJob(String token, String graphqlQuery) {
        String url = "https://apis.indeed.com/graphql";

        // Create request body
        GraphQLRequest request = new GraphQLRequest(graphqlQuery, null);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request entity
        HttpEntity<GraphQLRequest> entity = new HttpEntity<>(request, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}
