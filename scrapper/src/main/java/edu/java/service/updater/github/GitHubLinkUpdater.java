package edu.java.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.github.RepositoryNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.github.event.GitHubEventHandler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Getter
public class GitHubLinkUpdater implements LinkUpdater {
    private final String supportDomain = "github.com";

    private final GitHubClient gitHubWebClient;

    private final int userIndex = 2;

    private final int repositoryIndex = 3;

    private final Map<String, GitHubEventHandler> eventHandlers;

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    public GitHubLinkUpdater(GitHubClient gitHubWebClient, List<GitHubEventHandler> eventHandlers) {
        this.gitHubWebClient = gitHubWebClient;
        this.eventHandlers = new HashMap<>();
        eventHandlers.forEach(handler -> this.eventHandlers.put(handler.getEventTypeName(), handler));
    }

    @Override
    public Optional<LinkUpdates> fetchUpdates(Link link) throws RepositoryNotExistsException {
        var userAndRepository = getUserAndRepository(link.getUrl());

        LinkUpdates linkUpdates = new LinkUpdates(link.getId(), link.getUrl(), HttpStatus.OK,
            link.getLastUpdateTime(), new ArrayList<>(), new ArrayList<>());

        try {
            List<EventResponse> events =
                gitHubWebClient.fetchRepositoryEvents(userAndRepository.getKey(), userAndRepository.getValue());
            var newEvents =
                events.stream().filter(event -> event.createdAt().isAfter(link.getLastUpdateTime())).toList();

            if (!newEvents.isEmpty()) {
                this.eventHandlers.forEach((eventType, handler) -> {
                    var currentTypeEvents =
                        newEvents.stream().filter(event -> event.type().equals(eventType)).toList();

                    Optional<Update> update = handler.fetchUpdate(currentTypeEvents);
                    update.ifPresent(u -> addUpdate(linkUpdates, u));
                });
            }

        } catch (RepositoryNotExistsException exception) {
            LOGGER.error(exception.getResponse());
            linkUpdates.getUpdates().clear();
            linkUpdates.setHttpStatus(HttpStatus.GONE);

            linkUpdates.getUpdates().add(
                new Update(
                    "Репозиторий больше не существует, либо стал приватным :(\nСсылка будет удалена.",
                    OffsetDateTime.now())
            );
        }

        if (linkUpdates.getUpdates().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(linkUpdates);
    }

    @Override
    public void checkThatLinkExists(Link link) throws RepositoryNotExistsException {
        var repositoryData = getUserAndRepository(link.getUrl());

        RepositoryInfoResponse response =
            gitHubWebClient.checkThatRepositoryExists(repositoryData.getKey(), repositoryData.getValue());
        LOGGER.info("Checks repository: %s".formatted(response));
    }

    private Pair<String, String> getUserAndRepository(String url) {
        String[] urlParts = url.split("/+");
        return Pair.of(urlParts[userIndex], urlParts[repositoryIndex]);
    }
}
