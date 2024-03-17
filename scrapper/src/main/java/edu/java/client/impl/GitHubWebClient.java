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
    @Value("${api.github.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    @Value("${api.github.personal-access-token}")
    private String personalAccessToken;

    private final String repositoryPath = "/repos/%s/%s";

    private final String eventsPath = "/repos/%s/%s/events";

    private final String perPageParam = "per_page";

    @Value("${api.github.events-count}")
    private int eventsCount;

    private final String bearerPrefix = "Bearer %s";

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
            .get().uri(repositoryPath.formatted(user, repository))
            .header(HttpHeaders.AUTHORIZATION, bearerPrefix.formatted(personalAccessToken))
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
                .path(eventsPath.formatted(user, repository))
                .queryParam(perPageParam, eventsCount).build())
            .header(HttpHeaders.AUTHORIZATION, bearerPrefix.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .bodyToMono(new ParameterizedTypeReference<>() { });

        return events.block();
    }
}
