package edu.java.client.impl;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowWebClient implements StackOverflowClient {
    private String baseUrl = "https://api.stackexchange.com/2.3";
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
    public QuestionResponse fetchQuestion(Long question) {
        return webClient
            .get()
            .uri(builder -> builder
                .path("/questions/%d".formatted(question))
                .queryParam("site", "stackoverflow").build())
            .retrieve().bodyToMono(QuestionResponse.class).block();
    }
}
