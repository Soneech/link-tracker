package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.GitHubErrorResponse;
import edu.java.dto.github.response.PullRequestEventResponse;
import edu.java.dto.github.response.PushEventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubWebClient implements GitHubClient {
    @Value("${api.github.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    @Value("${api.github.personal-access-token}")
    private String personalAccessToken;

    private final String repositoryPath = "/repos/%s/%s";

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
    public PushEventResponse fetchRepositoryPushEvent(String user, String repository) {
        return webClient
            .get().uri(repositoryPath.formatted(user, repository))
            .header(HttpHeaders.AUTHORIZATION, bearerPrefix.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .bodyToMono(PushEventResponse.class)
            .block();
    }

    @Override
    public List<PullRequestEventResponse> fetchRepositoryPullEvent(String user, String repository) {
        Mono<List<PullRequestEventResponse>> response = webClient
            .get().uri(repositoryPath.formatted(user, repository) + "/pulls")
            .header(HttpHeaders.AUTHORIZATION, bearerPrefix.formatted(personalAccessToken))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                errorResponse -> errorResponse.bodyToMono(GitHubErrorResponse.class)
                    .map(RepositoryNotExistsException::new))
            .bodyToMono(new ParameterizedTypeReference<>() { });

        return response.block();
    }
}
