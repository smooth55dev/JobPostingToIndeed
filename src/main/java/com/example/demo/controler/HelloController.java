package com.example.demo.controler;

import okhttp3.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@RestController
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final OAuthService oauthService;  

    @Autowired
    public HelloController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }
    
    @GetMapping("/")
    public String index() {
        try {
            String accessToken = oauthService.getAccessToken().block();

            String employerId = oauthService.getEmployer(accessToken).block();
            String getEmployerAccessToken = oauthService.getEmployerAccessToken(employerId).block();
            logger.info("Employer Access Token: {}", getEmployerAccessToken);
        //     return "ok";
        // } catch (Exception e) {
        //     logger.error("Error occurred while making the request. : {}", e);
        //     return "Error occurred while making the request.";
        // }
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\n  \"address\": \"10721 Domain Dr. Austin TX, 78758\",\n  \"applyMethod\": {\n    \"emails\": \"[\\\"example1@indeed.com\\\", \\\"example2@indeed.com\\\"]\",\n    \"method\": \"EMAIL\"\n  },\n  \"company\": \"My Company\",\n  \"country\": \"US\",\n  \"coverLetterRequired\": \"OPTIONAL\",\n  \"description\": \"My job description.\",\n  \"displayLanguage\": \"en\",\n  \"jobLocations\": [\n    {\n      \"active\": true,\n      \"location\": \"Austin, TX\"\n    }\n  ],\n  \"jobTypes\": \"[\\\"FULL_TIME\\\", \\\"INTERNSHIP\\\"]\",\n  \"jobUuid\": \"5f2b2763b09cb25b7e3e0a76\",\n  \"phoneRequired\": \"OPTIONAL\",\n  \"resumeRequired\": \"OPTIONAL\",\n  \"salary\": {\n    \"maximum\": 50,\n    \"minimum\": 30,\n    \"period\": \"HOUR\"\n  },\n  \"status\": \"ACTIVE\",\n  \"title\": \"Software Engineer\"\n}";
            
            RequestBody body = RequestBody.create(mediaType, requestBody);
            Request request = new Request.Builder()
                    .url("https://employers.indeed.com/api/v2/jobs")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();
            
            logger.info("Request: {}", request);
            
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    logger.info("Response Body: {}", responseBody);
                    return responseBody;
                } else {
                    String responseBody = response.body().string();
                    logger.error("Failed to post job: {}", responseBody);
                    return "Failed to post job: " + responseBody;
                }
            }
        } catch (IOException e) {
            logger.error("Error occurred while making the request.", e);
            return "Error occurred while making the request.";
        }
    }
}
