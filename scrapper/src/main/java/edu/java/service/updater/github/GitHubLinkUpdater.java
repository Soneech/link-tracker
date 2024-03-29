package edu.java.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.ResourceUnavailableException;
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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class GitHubLinkUpdater implements LinkUpdater {

    private final GitHubClient gitHubWebClient;

    private final Map<String, GitHubEventHandler> eventHandlers;

    private static final String SUPPORT_DOMAIN = "github.com";

    private static final String REPOSITORY_NOT_FOUND_MESSAGE =
        "Репозиторий больше не существует, либо стал приватным :(\nСсылка будет удалена.";

    private static final int USER_INDEX = 2;

    private static final int REPOSITORY_INDEX = 3;

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

        LinkUpdates linkUpdates = LinkUpdates.builder()
            .linkId(link.getId())
            .url(link.getUrl())
            .httpStatus(HttpStatus.OK)
            .lastUpdateTime(link.getLastUpdateTime())
            .updates(new ArrayList<>()).tgChatIds(new ArrayList<>())
            .build();

        try {
            List<EventResponse> newEvents = fetchEventsAndGetNew(userAndRepository.getKey(),
                userAndRepository.getValue(), link.getLastUpdateTime());

            if (!newEvents.isEmpty()) {
                this.eventHandlers.forEach((eventType, handler) -> {
                    Optional<Update> update = fetchUpdateForEventType(newEvents, eventType, handler);
                    update.ifPresent(u -> addUpdate(linkUpdates, u));
                });
            }
        } catch (RepositoryNotExistsException exception) {
            LOGGER.error(exception.getResponse());
            addResourceNotFoundUpdate(linkUpdates, REPOSITORY_NOT_FOUND_MESSAGE);
        }

        if (CollectionUtils.isEmpty(linkUpdates.getUpdates())) {
            return Optional.empty();
        }

        return Optional.of(linkUpdates);
    }

    @Override
    public String getSupportDomain() {
        return SUPPORT_DOMAIN;
    }

    @Override
    public void checkThatLinkExists(Link link) throws RepositoryNotExistsException, ResourceUnavailableException {
        var repositoryData = getUserAndRepository(link.getUrl());

        RepositoryInfoResponse response =
            gitHubWebClient.checkThatRepositoryExists(repositoryData.getKey(), repositoryData.getValue());
        LOGGER.info("Checks repository: %s".formatted(response));
    }

    public Pair<String, String> getUserAndRepository(String url) {
        String[] urlParts = url.split("/+");
        return Pair.of(urlParts[USER_INDEX], urlParts[REPOSITORY_INDEX]);
    }

    public List<EventResponse> fetchEventsAndGetNew(String username, String repository, OffsetDateTime lastUpdateTime) {
        List<EventResponse> events = gitHubWebClient.fetchRepositoryEvents(username, repository);
        return events.stream()
            .filter(event -> event.createdAt().isAfter(lastUpdateTime)).toList();
    }

    public Optional<Update> fetchUpdateForEventType(List<EventResponse> newEvents,
        String eventTypeName, GitHubEventHandler handler) {

        var currentTypeEvents =
            newEvents.stream().filter(event -> event.type().equals(eventTypeName)).toList();
        return handler.fetchUpdate(currentTypeEvents);
    }
}
