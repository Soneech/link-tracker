package edu.java.client.impl;

import edu.java.client.GitHubClient;
import edu.java.dto.github.RepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubWebClient implements GitHubClient {
    private String baseUrl = "https://api.github.com";
    private final WebClient webClient;

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
            .retrieve()
            .bodyToMono(RepositoryResponse.class)
            .block();
    }
}
