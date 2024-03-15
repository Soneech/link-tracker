package edu.java.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.RepositoryPushEventResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.github.RepositoryNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.github.event.GitHubEventHandler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final String supportDomain = "github.com";

    private final GitHubClient gitHubWebClient;

    private final int userIndex = 2;

    private final int repositoryIndex = 3;

    private final List<GitHubEventHandler> eventHandlers;

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<LinkUpdates> fetchUpdates(Link link) throws RepositoryNotExistsException {
        var repositoryData = getUserAndRepository(link.getUrl());

        LinkUpdates linkUpdates = new LinkUpdates(link.getId(), link.getUrl(), HttpStatus.OK,
            link.getLastUpdateTime(), new ArrayList<>(), new ArrayList<>());

        try {
            eventHandlers.forEach(handler -> {
                Optional<Update> update = handler.fetchUpdate(repositoryData, link);
                update.ifPresent(u -> {
                    linkUpdates.getUpdates().add(u);
                    if (u.updateTime().isAfter(linkUpdates.getLastUpdateTime())) {
                        linkUpdates.setLastUpdateTime(u.updateTime());
                    }
                });
            });

        } catch (RepositoryNotExistsException exception) {
            LOGGER.error(exception.getResponse());
            linkUpdates.getUpdates().clear();
            linkUpdates.setHttpStatus(HttpStatus.GONE);

            linkUpdates.getUpdates().add(
                new Update("Репозиторий больше не существует :(. Ссылка будет удалена.",
                    OffsetDateTime.now())
            );
        }

        if (linkUpdates.getUpdates().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(linkUpdates);
    }

    @Override
    public void setLastUpdateTime(Link link) {
        var repositoryData = getUserAndRepository(link.getUrl());

        RepositoryPushEventResponse response =
            gitHubWebClient.fetchRepositoryPushEvent(repositoryData.getKey(), repositoryData.getValue());
        link.setLastUpdateTime(response.pushedAt());
    }

    private Pair<String, String> getUserAndRepository(String url) {
        String[] urlParts = url.split("/+");
        return Pair.of(urlParts[userIndex], urlParts[repositoryIndex]);
    }
}
