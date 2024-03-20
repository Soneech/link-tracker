package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.GitHubErrorResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import java.util.List;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Setter
public class GitHubWebClient implements GitHubClient {

    private final WebClient webClient;

    @Value("${api.github.base-url}")
    private String baseUrl;

    @Value("${api.github.personal-access-token}")
    private String personalAccessToken;

    @Value("${api.github.events-count}")
    private int eventsCount;

    private static final String REPOSITORY_PATH = "/repos/";

    private static final String EVENTS_PATH = "/events";

    private static final String PER_PAGE_PARAM = "per_page";

    private static final String BEARER_PREFIX = "Bearer %s";

    public GitHubWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public GitHubWebClient(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            this.baseUrl = baseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.baseUrl).build();
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
}
