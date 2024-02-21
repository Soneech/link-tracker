package edu.java.scheduler;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final StackOverflowClient stackOverflowWebClient;

    private final GitHubClient gitHubWebClient;

    private final String gitHubUserName = "swagger-api";

    private final String repositoryName = "swagger-core";

    private final Long questionId = 46947633L;

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        // for example
        LOGGER.info("Getting updates...");
        LOGGER.info("GitHub update: " + gitHubWebClient.fetchRepository(gitHubUserName, repositoryName));
        LOGGER.info("Stack overflow update: " + stackOverflowWebClient.fetchQuestion(questionId));
    }
}
