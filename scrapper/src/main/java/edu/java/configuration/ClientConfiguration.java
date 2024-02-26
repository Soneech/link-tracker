package edu.java.configuration;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.impl.GitHubWebClient;
import edu.java.client.impl.StackOverflowWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${api.github.base-url}")
    private String gitHubBaseUrl;

    @Value("${api.stackoverflow.base-url}")
    private String stackOverflowBaseUrl;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubWebClient(gitHubBaseUrl);
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowWebClient(stackOverflowBaseUrl);
    }
}
