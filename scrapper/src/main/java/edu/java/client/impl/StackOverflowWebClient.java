package edu.java.client.impl;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowWebClient implements StackOverflowClient {
    @Value("${api.stackoverflow.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    public StackOverflowWebClient() {
        webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    public StackOverflowWebClient(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override
    public QuestionResponse fetchQuestion(Long question) {  // нужно добавить обработку 4хх ответов
        return webClient
            .get()
            .uri(builder -> builder
                .path("/questions/%d".formatted(question))
                .queryParam("site", "stackoverflow").build())
            .retrieve()
            .bodyToMono(QuestionResponse.class).block();
    }
}
