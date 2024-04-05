package edu.java.client.impl;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.AnswersResponse;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.exception.ResourceUnavailableException;
import java.util.List;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Setter
public class StackOverflowWebClient implements StackOverflowClient {

    private final WebClient webClient;

    @Value("${api.stackoverflow.base-url}")
    private String baseUrl;

    @Value("${retry.stackoverflow.retry-status-codes}")
    private List<HttpStatus> retryStatusCodes;

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

    @Override  // exponential backoff
    @Retryable(retryFor = ResourceUnavailableException.class,
               maxAttemptsExpression = "${retry.stackoverflow.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.stackoverflow.delay}",
                                  multiplierExpression = "${retry.stackoverflow.multiplier}"))
    public QuestionResponse fetchQuestion(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(QUESTION_PATH).path(String.valueOf(questionId))
                .queryParam(SITE_PARAM.getKey(), SITE_PARAM.getValue()).build())
            .retrieve()
            .onStatus(
                statusCode -> retryStatusCodes.contains(statusCode),
                response -> Mono.error(new ResourceUnavailableException(response.statusCode()))
            )
            .bodyToMono(QuestionResponse.class)
            .block();
    }

    @Override  // random backoff
    @Retryable(retryFor = ResourceUnavailableException.class,
               maxAttemptsExpression = "${retry.stackoverflow.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.stackoverflow.delay}",
                                  maxDelayExpression = "${retry.stackoverflow.max-delay}", random = true))
    public AnswersResponse fetchQuestionAnswers(Long questionId) {
        return webClient
            .get()
            .uri(builder -> builder
                .path(QUESTION_PATH).path(String.valueOf(questionId)).path("/").path(ANSWERS_PATH)
                .queryParam(SITE_PARAM.getKey(), SITE_PARAM.getValue())
                .queryParam(SORT_PARAM.getKey(), SORT_PARAM.getValue()).build())
            .retrieve()
            .onStatus(
                statusCode -> retryStatusCodes.contains(statusCode),
                response -> Mono.error(new ResourceUnavailableException(response.statusCode()))
            )
            .bodyToMono(AnswersResponse.class)
            .block();
    }
}
