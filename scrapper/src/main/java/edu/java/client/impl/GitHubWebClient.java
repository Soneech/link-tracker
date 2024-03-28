package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.client.retry.RetryPolicy;
import edu.java.client.retry.RetryPolicyHolder;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.GitHubErrorResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import edu.java.util.StackTraceUtil;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Setter
@RequiredArgsConstructor
public class GitHubWebClient implements GitHubClient {

    private final WebClient webClient;

    private final RetryPolicyHolder retryPolicyHolder;

    private final String personalAccessToken;

    @Value("${api.github.base-url}")
    private String baseUrl;

    @Value("${api.github.events-count}")
    private int eventsCount;

    @Value("${api.github.retry.policy}")
    private String retryPolicyName;

    @Value("${api.github.retry.error-status-codes}")
    private List<HttpStatus> errorStatusCodes;

    private Retry retry;

    private static final String REPOSITORY_PATH = "/repos/";

    private static final String EVENTS_PATH = "/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String BEARER_PREFIX = "Bearer %s";

    private static final Logger LOGGER = LogManager.getLogger();


    public GitHubWebClient(RetryPolicyHolder retryPolicyHolder, String personalAccessToken) {
        this.retryPolicyHolder = retryPolicyHolder;
        this.personalAccessToken = personalAccessToken;

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public GitHubWebClient(RetryPolicyHolder retryPolicyHolder, String personalAccessToken, String baseUrl) {
        this.retryPolicyHolder = retryPolicyHolder;
        this.personalAccessToken = personalAccessToken;

        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override
    @PostConstruct
    public void initializeRetry() {
        RetryPolicy retryPolicy = retryPolicyHolder.getRetryPolicyByName(this.retryPolicyName);
        retry = retryPolicy.setUpRetry(errorStatusCodes);
    }

    @Override
    public RepositoryInfoResponse checkThatRepositoryExists(String user, String repository) {
        return webClient
            .get().uri(
                builder -> builder.path(REPOSITORY_PATH).path(user).path("/").path(repository).build())
            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .bodyToMono(RepositoryInfoResponse.class)
            .block();
    }

    @Override
    public RepositoryInfoResponse checkThatRepositoryExistsWithRetry(String user, String repository) {
        try {
            return Retry.decorateSupplier(retry, () -> checkThatRepositoryExists(user, repository)).get();
        } catch (WebClientResponseException exception) {
            LOGGER.error("Retry error: resource unavailable; status: %s".formatted(exception.getStatusCode()));
            return null;
            // TODO
        }
    }

    @Override
    public List<EventResponse> fetchRepositoryEvents(String user, String repository) {
        Mono<List<EventResponse>> events = webClient
            .get()
            .uri(builder -> builder
                .path(REPOSITORY_PATH).path(user).path("/").path(repository).path("/").path(EVENTS_PATH)
                .queryParam(PER_PAGE_PARAM, eventsCount).build())
            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .bodyToMono(new ParameterizedTypeReference<>() { });

        return events.block();
    }

    @Override
    public List<EventResponse> fetchRepositoryEventsWithRetry(String user, String repository) {
        try {
            return Retry.decorateSupplier(retry, () -> fetchRepositoryEvents(user, repository)).get();
        } catch (WebClientResponseException exception) {
            LOGGER.error("Retry error with status: %s; stack trace:\n%s"
                .formatted(exception.getStatusCode(), String.join("\n", StackTraceUtil.getStackTrace(exception))));
            return Collections.emptyList();
        }
    }
}
