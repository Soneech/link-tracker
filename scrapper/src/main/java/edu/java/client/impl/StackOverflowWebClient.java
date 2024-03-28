package edu.java.client.impl;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowWebClient implements StackOverflowClient {
    @Value("${api.stackoverflow.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    private static final String QUESTION_PATH = "/questions/";

    private static final String ANSWERS_PATH = "/answers";

    private static final Pair<String, String> SITE_PARAM = Pair.of("site", "stackoverflow");

    private static final Pair<String, String> SORT_PARAM = Pair.of("sort", "creation");

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
    @PostConstruct
    public void initializeRetry() {

    }

    @Override
    public QuestionResponse fetchQuestion(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(QUESTION_PATH).path(String.valueOf(questionId))
                .queryParam(SITE_PARAM.getKey(), SITE_PARAM.getValue()).build())
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }

    @Override
    public QuestionResponse fetchQuestionAnswers(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(QUESTION_PATH).path(String.valueOf(questionId)).path("/").path(ANSWERS_PATH)
                .queryParam(SITE_PARAM.getKey(), SITE_PARAM.getValue())
                .queryParam(SORT_PARAM.getKey(), SORT_PARAM.getValue()).build())
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }
}
