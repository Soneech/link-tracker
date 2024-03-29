package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.GitHubErrorResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.ResourceUnavailableException;
import edu.java.exception.github.RepositoryNotExistsException;
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Setter
@RequiredArgsConstructor
public class GitHubWebClient implements GitHubClient {

    private final WebClient webClient;

    @Value("${api.github.base-url}")
    private String baseUrl;  // можно менять

    @Value("${retry.github.error-status-codes}")
    private List<HttpStatus> errorStatusCodes;

    private final String personalAccessToken;

    private final int eventsCount;

    private static final String REPOSITORY_PATH = "/repos/";

    private static final String EVENTS_PATH = "/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String BEARER_PREFIX = "Bearer %s";

    private static final Logger LOGGER = LogManager.getLogger();

    public GitHubWebClient(String personalAccessToken, int eventsCount) {
        this.personalAccessToken = personalAccessToken;
        this.eventsCount = eventsCount;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public GitHubWebClient(String baseUrl, String personalAccessToken, int eventsCount) {
        this.personalAccessToken = personalAccessToken;
        this.eventsCount = eventsCount;
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override  // exponential backoff
    @Retryable(retryFor = ResourceUnavailableException.class, maxAttemptsExpression = "${retry.github.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.github.delay}",
                                  multiplierExpression = "${retry.github.multiplier}"))
    public RepositoryInfoResponse checkThatRepositoryExists(String user, String repository) {
        return webClient
            .get().uri(
                builder -> builder.path(REPOSITORY_PATH).path(user).path("/").path(repository).build())
            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .onStatus(
                statusCode -> errorStatusCodes.contains(statusCode),
                response -> Mono.error(new ResourceUnavailableException(response.statusCode()))
            )
            .bodyToMono(RepositoryInfoResponse.class)
            .block();
    }

    @Override  // random backoff
    @Retryable(retryFor = ResourceUnavailableException.class, maxAttemptsExpression = "${retry.github.max-attempts}",
               backoff = @Backoff(delayExpression = "${retry.github.delay}",
                                  maxDelayExpression = "${retry.github.max-delay}", random = true))
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
            .onStatus(
                statusCode -> errorStatusCodes.contains(statusCode),
                response -> Mono.error(new ResourceUnavailableException(response.statusCode())))
            .bodyToMono(new ParameterizedTypeReference<>() { });

        return events.block();
    }

    @Recover
    public List<EventResponse> recoverFetchRepositoryEvents(ResourceUnavailableException exception,
        String user, String repository) {
        LOGGER.error("Cannot get response from repository: %s/%s; status code: %s"
            .formatted(user, repository, exception.getHttpStatusCode()));
        return Collections.emptyList();
    }
}
