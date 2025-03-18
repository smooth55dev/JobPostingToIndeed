package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class OAuthService {

    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);

    @Value("${indeed.client.authentication.id}")
    private String authenticationId;

    @Value("${indeed.client.authentication.secret}")
    private String authenticationSecret;

    @Value("${oauth.token-url}")
    private String tokenUrl;

    @Value("${appinfo.url}")
    private String appInfoUrl;

    @Value("${userinfo.url}")
    private String userInfoUrl;

    public OAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // Get or refresh 2-legged access token
    public Mono<String> getAccessToken() {
        return webClient.post()
            .uri(tokenUrl)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                    .with("client_id", authenticationId)
                    .with("client_secret", authenticationSecret)
                    .with("scope", "employer_access"))
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                return clientResponse.bodyToMono(String.class).flatMap(body -> {
                    return Mono.error(new RuntimeException("Client error: " + body));
                });
            })
            .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                return clientResponse.bodyToMono(String.class).flatMap(body -> {
                    return Mono.error(new RuntimeException("Server error: " + body));
                });
            })
            .bodyToMono(Map.class)
            .map(response -> (String) response.get("access_token"));
    }
    
    // Build employer selection screen
    public Mono<String> getEmployer(String accessToken) {
        return webClient.post()
        .uri(appInfoUrl)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), clientResponse -> {
            return clientResponse.bodyToMono(String.class).flatMap(body -> {
                return Mono.error(new RuntimeException("Employer Client error: " + body));
            });
        })
        .onStatus(status -> status.is5xxServerError(), clientResponse -> {
            return clientResponse.bodyToMono(String.class).flatMap(body -> {
                return Mono.error(new RuntimeException("Employer Server error: " + body));
            });
        })
        .bodyToMono(Map.class)
        .map(response -> {
            List<Map<String, String>> employers = (List<Map<String, String>>) response.get("employers");
            if (employers != null && !employers.isEmpty()) {
                return employers.get(0).get("id");
            } else {
                throw new RuntimeException("No employers found in the response");
            }
        });
    }
    
    // Get employer access token
    public Mono<String> getEmployerAccessToken(String employerId) {
        return webClient.post()
            .uri(tokenUrl)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/json")
            .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                    .with("client_id", authenticationId)
                    .with("client_secret", authenticationSecret)
                    .with("employer", employerId)
                    .with("scope", "employer_access"))
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                return clientResponse.bodyToMono(String.class).flatMap(body -> {
                    return Mono.error(new RuntimeException("Client error: " + body));
                });
            })
            .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                return clientResponse.bodyToMono(String.class).flatMap(body -> {
                    return Mono.error(new RuntimeException("Server error: " + body));
                });
            })
            .bodyToMono(Map.class)
            .map(response -> (String) response.get("access_token"));
    }

    // Build user info section
    public Mono<String> getUserInfo(String accessToken) {
        return webClient.post()
        .uri(userInfoUrl)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(), clientResponse -> {
            logger.error("4xx Client Error: {}", clientResponse.statusCode());
            return clientResponse.bodyToMono(String.class).flatMap(body -> {
                logger.error("Client error body: {}", body.toString());
                return Mono.error(new RuntimeException("Employer Client error: " + body));
            });
        })
        .onStatus(status -> status.is5xxServerError(), clientResponse -> {
            logger.error("5xx Server Error: {}", clientResponse.statusCode());
            return clientResponse.bodyToMono(String.class).flatMap(body -> {
                logger.error("Server error body: {}", body.toString());
                return Mono.error(new RuntimeException("Employer Server error: " + body));
            });
        })
        .bodyToMono(Map.class)
        .doOnNext(response -> logger.debug("Response received: {}", response))
        .map(response -> {
            String sub = (String) response.get("sub");
            logger.debug("Extracted sub: {}", sub);
            return sub;
        })
        .doOnError(error -> logger.error("Error in getUserInfo: {}", error.getMessage(), error));
    }
    
}
