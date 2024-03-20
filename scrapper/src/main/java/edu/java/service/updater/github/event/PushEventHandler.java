package edu.java.service.updater.github.event;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.update.Update;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushEventHandler implements GitHubEventHandler {

    private static final String EVENT_TYPE_NAME = "PushEvent";

    private static final String NEW_ONE_COMMIT_MESSAGE = "Новый коммит от пользователя %s  (создан в %s)\n";

    private static final String NEW_SEVERAL_COMMITS_MESSAGE =
        "Новых коммитов: %d от пользователя %s (последний создан в %s)";

    @Override
    public Optional<Update> fetchUpdate(List<EventResponse> newEvents) {
        if (newEvents.isEmpty()) {
            return Optional.empty();
        }

        String eventsDescriptions = "";
        OffsetDateTime newLastUpdateTime = OffsetDateTime.MIN;

        Map<String, List<EventResponse>> authorsAndCommits = getAuthorsAndCommits(newEvents);

        for (var authorAndCommits: authorsAndCommits.entrySet()) {
            var author = authorAndCommits.getKey();
            var commits = authorAndCommits.getValue();

            OffsetDateTime currentLastUpdateTime = commits.getFirst().createdAt();

            if (commits.size() == 1) {
                eventsDescriptions = getDescriptionForOneCommitUpdate(author, currentLastUpdateTime);
            } else {
                currentLastUpdateTime = getCommitsLastUpdateTime(commits, currentLastUpdateTime);
                eventsDescriptions =
                    getDescriptionForSeveralCommitsUpdate(author, commits.size(), currentLastUpdateTime);
            }

            if (currentLastUpdateTime.isAfter(newLastUpdateTime)) {
                newLastUpdateTime = currentLastUpdateTime;
            }
        }

        return Optional.of(new Update(eventsDescriptions, newLastUpdateTime));
    }

    @Override
    public String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }

    public String getPrettyDateTime(OffsetDateTime dateTime) {
        return "%s, %s UTC".formatted(dateTime.toLocalTime(), dateTime.toLocalDate());
    }

    public Map<String, List<EventResponse>> getAuthorsAndCommits(List<EventResponse> newEvents) {
        return newEvents.stream()
            .collect(Collectors.groupingBy(event -> event.actor().login()));
    }

    public String getDescriptionForOneCommitUpdate(String author, OffsetDateTime updateTime) {
        return NEW_ONE_COMMIT_MESSAGE.formatted(author, getPrettyDateTime(updateTime));
    }

    public String getDescriptionForSeveralCommitsUpdate(String author, int commitsCount,
        OffsetDateTime lastUpdateTime) {

        return NEW_SEVERAL_COMMITS_MESSAGE
            .formatted(commitsCount, author, getPrettyDateTime(lastUpdateTime));
    }

    public OffsetDateTime getCommitsLastUpdateTime(List<EventResponse> commits,
        OffsetDateTime currentLastUpdateTime) {

        OffsetDateTime latestUpdateTime = currentLastUpdateTime;
        for (var commit: commits) {
            if (commit.createdAt().isAfter(latestUpdateTime)) {
                latestUpdateTime = commit.createdAt();
            }
        }
        return latestUpdateTime;
    }
}
