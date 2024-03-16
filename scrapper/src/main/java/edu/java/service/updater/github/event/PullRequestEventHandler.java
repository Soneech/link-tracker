package edu.java.service.updater.github.event;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.PullRequestEventResponse;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PullRequestEventHandler implements GitHubEventHandler {

    private final GitHubClient gitHubWebClient;

    @Override
    public Optional<Update> fetchUpdate(Pair<String, String> userAndRepository, Link link) {
        List<PullRequestEventResponse> response =
            gitHubWebClient.fetchRepositoryPullEvent(userAndRepository.getKey(), userAndRepository.getValue());

        OffsetDateTime newLastUpdateTime = link.getLastUpdateTime();
        StringBuilder description = new StringBuilder();

        for (var pullEvent: response) {
            if (pullEvent.createdAt().isAfter(link.getLastUpdateTime())) {
                description.append("Появлися новый pull request: %s от пользователя %s"
                    .formatted(pullEvent.title(), pullEvent.user().login())).append("\n");
                newLastUpdateTime = getNewLastUpdateTime(newLastUpdateTime, pullEvent.createdAt());

            } else if (pullEvent.updatedAt().isAfter(link.getLastUpdateTime())) {
                description.append("Появилось обновление в pull request: %s"
                    .formatted(pullEvent.title())).append("\n");
                newLastUpdateTime = getNewLastUpdateTime(newLastUpdateTime, pullEvent.updatedAt());
            }
        }

        if (!description.isEmpty()) {
            return Optional.of(new Update(description.toString(), newLastUpdateTime));
        }
        return Optional.empty();
    }

    public OffsetDateTime getNewLastUpdateTime(OffsetDateTime oldValue, OffsetDateTime newValue) {
        if (newValue.isAfter(oldValue)) {
            return newValue;
        }
        return oldValue;
    }
}
