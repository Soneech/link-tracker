package edu.java.service.updater;

import edu.java.client.GitHubClient;
import edu.java.dto.github.RepositoryResponse;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final String supportDomain = "github.com";

    private final GitHubClient gitHubWebClient;

    private final int userIndex = 2;

    private final int repositoryIndex = 3;

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) {
        var repositoryData = getUserAndRepository(link.getUrl());
        RepositoryResponse response =
            gitHubWebClient.fetchRepository(repositoryData.getKey(), repositoryData.getValue());

        Optional<Update> update = Optional.empty();
        if (response.pushedAt().isAfter(link.getLastUpdateTime())) {
            update = Optional.of(
                new Update(link.getId(), link.getUrl(),
                    "появился новый коммит.", response.pushedAt())
            );
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
