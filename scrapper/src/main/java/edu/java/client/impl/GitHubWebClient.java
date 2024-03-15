package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.dto.github.GitHubErrorResponse;
import edu.java.dto.github.RepositoryResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubWebClient implements GitHubClient {
    @Value("${api.github.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    @Value("${api.github.personal-access-token}")
    private String personalAccessToken;

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
    public RepositoryResponse fetchRepository(String user, String repository) {
        return webClient
            .get().uri("/repos/%s/%s".formatted(user, repository))
            .header("Authorization", "Bearer " + personalAccessToken)
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(GitHubErrorResponse.class).map(RepositoryNotExistsException::new))
            .bodyToMono(RepositoryResponse.class)
            .block();
    }
}
