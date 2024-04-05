package edu.java.configuration.client;

import edu.java.client.BotClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.impl.BotWebClient;
import edu.java.client.impl.GitHubWebClient;
import edu.java.client.impl.StackOverflowWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Value("${api.github.base-url}")
    private String gitHubBaseUrl;

    @Value("${api.stackoverflow.base-url}")
    private String stackOverflowBaseUrl;

    @Value("${api.bot.base-url}")
    private String botBaseUrl;

    @Value("${api.github.personal-access-token:some-token}")
    String gitHubPersonalAccessToken;

    @Value("${api.github.events-count}")
    int eventsCount;

    @Bean
    public GitHubClient gitHubWebClient() {
        return new GitHubWebClient(gitHubBaseUrl, gitHubPersonalAccessToken, eventsCount);
    }

    @Bean
    public StackOverflowClient stackOverflowWebClient() {
        return new StackOverflowWebClient(stackOverflowBaseUrl);
    }

    @Bean
    public BotClient botWebClient() {
        return new BotWebClient(botBaseUrl);
    }
}
