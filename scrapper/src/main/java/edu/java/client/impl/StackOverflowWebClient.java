package edu.java.client.impl;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowWebClient implements StackOverflowClient {
    @Value("${api.stackoverflow.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    private final String questionPath = "/questions/%d";

    private final String answersPath = "/questions/%d/answers";

    private final Pair<String, String> siteParam = Pair.of("site", "stackoverflow");

    private final Pair<String, String> sortParam = Pair.of("sort", "creation");

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
    public QuestionResponse fetchQuestion(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(questionPath.formatted(questionId))
                .queryParam(siteParam.getKey(), siteParam.getValue()).build())
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }

    @Override
    public QuestionResponse fetchQuestionAnswers(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(answersPath.formatted(questionId))
                .queryParam(siteParam.getKey(), siteParam.getValue())
                .queryParam(sortParam.getKey(), sortParam.getValue()).build())
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }
}
