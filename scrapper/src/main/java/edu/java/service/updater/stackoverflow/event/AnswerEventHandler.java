package edu.java.service.updater.stackoverflow.event;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerEventHandler implements StackOverflowEventHandler {

    private final StackOverflowClient stackOverflowWebClient;

    @Override
    public Optional<Update> fetchUpdate(Long questionId, Link link) {
        QuestionResponse response = stackOverflowWebClient.fetchQuestionAnswers(questionId);

        if (!response.items().isEmpty()) {
            StringBuilder updateDescriptions = new StringBuilder();
            OffsetDateTime newLastUpdateTime = link.getLastUpdateTime();

            for (var answer: response.items()) {
                if (answer.creationDate().isAfter(link.getLastUpdateTime())) {
                    updateDescriptions
                        .append("Новый ответ от пользователя %s".formatted(answer.owner().name()))
                        .append("\n");
                    if (answer.creationDate().isAfter(newLastUpdateTime)) {
                        newLastUpdateTime = answer.creationDate();
                    }
                } else if (answer.lastActivityDate().isAfter(link.getLastUpdateTime())) {
                    updateDescriptions
                        .append("Изменения в ответе от пользователя %s".formatted(answer.owner().name()))
                        .append("\n");
                    if (answer.lastActivityDate().isAfter(newLastUpdateTime)) {
                        newLastUpdateTime = answer.lastActivityDate();
                    }
                }
            }

            if (!updateDescriptions.isEmpty()) {
                return Optional.of(new Update(updateDescriptions.toString(), newLastUpdateTime));
            }
        }

        return Optional.empty();
    }
}
