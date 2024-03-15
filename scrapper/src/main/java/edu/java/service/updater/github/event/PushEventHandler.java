package edu.java.service.updater.github.event;

import edu.java.client.GitHubClient;
import edu.java.dto.github.RepositoryPushEventResponse;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushEventHandler implements GitHubEventHandler {

    private final GitHubClient gitHubWebClient;

    @Override
    public Optional<Update> fetchUpdate(Pair<String, String> userAndRepository, Link link) {
        RepositoryPushEventResponse response =
            gitHubWebClient.fetchRepositoryPushEvent(userAndRepository.getKey(), userAndRepository.getValue());

        if (response.pushedAt().isAfter(link.getLastUpdateTime())) {
            return Optional.of(new Update("Появился новый коммит.", response.pushedAt()));
        }
        return Optional.empty();
    }
}
