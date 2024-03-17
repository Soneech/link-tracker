package edu.java.service.updater.github.event;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.update.Update;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class PushEventHandler implements GitHubEventHandler {

    private final String eventTypeName = "PushEvent";

    @Override
    public Optional<Update> fetchUpdate(List<EventResponse> newEvents) {
        if (newEvents.isEmpty()) {
            return Optional.empty();
        }

        Map<String, List<EventResponse>> authorsAndCommits =
            newEvents.stream().collect(Collectors.groupingBy(event -> event.actor().login()));
        StringBuilder eventsDescriptions = new StringBuilder();
        OffsetDateTime newLastUpdateTime = OffsetDateTime.MIN;

        for (var authorAndCommits: authorsAndCommits.entrySet()) {
            var author = authorAndCommits.getKey();
            var commits = authorAndCommits.getValue();

            OffsetDateTime currentLastUpdateTime = commits.getFirst().createdAt();
            if (commits.size() == 1) {
                eventsDescriptions
                    .append("Новый коммит от пользователя %s (создан в %s)"
                        .formatted(author, getPrettyDateTime(currentLastUpdateTime))).append("\n");
            } else {
                for (var commit: commits) {
                    if (commit.createdAt().isAfter(currentLastUpdateTime)) {
                        currentLastUpdateTime = commit.createdAt();
                    }
                }
                eventsDescriptions
                    .append("Новых коммитов: %d от пользователя %s (последний создан в %s)"
                        .formatted(commits.size(), author, getPrettyDateTime(currentLastUpdateTime)))
                    .append("\n");
            }
            if (currentLastUpdateTime.isAfter(newLastUpdateTime)) {
                newLastUpdateTime = currentLastUpdateTime;
            }
        }

        return Optional.of(new Update(eventsDescriptions.toString(), newLastUpdateTime));
    }

    public String getPrettyDateTime(OffsetDateTime dateTime) {
        return "%s, %s UTC".formatted(dateTime.toLocalTime(), dateTime.toLocalDate());
    }
}
