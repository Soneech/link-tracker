package edu.java.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.RepositoryResponse;
import edu.java.dto.update.Update;
import edu.java.exception.github.RepositoryNotExistsException;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.Optional;
import edu.java.service.updater.LinkUpdater;
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

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) throws RepositoryNotExistsException {
        var repositoryData = getUserAndRepository(link.getUrl());

        Optional<Update> update = Optional.empty();

        try {
            RepositoryResponse response =
                gitHubWebClient.fetchRepository(repositoryData.getKey(), repositoryData.getValue());
            if (response.pushedAt().isAfter(link.getLastUpdateTime())) {
                update = Optional.of(
                    new Update(link.getId(), link.getUrl(),
                        "Появился новый коммит.", HttpStatus.OK, response.pushedAt())
                );
            }
        } catch (RepositoryNotExistsException exception) {
            LOGGER.error(exception.getResponse());
            update = Optional.of(
                new Update(link.getId(), link.getUrl(), "Репозиторий не существует :(. Ссылка будет удалена.",
                    HttpStatus.NOT_FOUND, OffsetDateTime.now()));
        }

        return update;
    }

    @Override
    public void setLastUpdateTime(Link link) {
        var repositoryData = getUserAndRepository(link.getUrl());

        RepositoryResponse response =
            gitHubWebClient.fetchRepository(repositoryData.getKey(), repositoryData.getValue());
        link.setLastUpdateTime(response.pushedAt());
    }

    private Pair<String, String> getUserAndRepository(String url) {
        String[] urlParts = url.split("/+");
        return Pair.of(urlParts[userIndex], urlParts[repositoryIndex]);
    }
}
