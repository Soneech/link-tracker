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

    private static final String NEW_ANSWER_MESSAGE = "Новый ответ от пользователя %s";

    private static final String UPDATE_ANSWER_MESSAGE = "Изменения в ответе от пользователя %s";

    @Override
    public Optional<Update> fetchUpdate(Long questionId, Link link) {
        QuestionResponse response = stackOverflowWebClient.fetchQuestionAnswers(questionId);

        if (!response.items().isEmpty()) {
            StringBuilder updateDescriptions = new StringBuilder();
            OffsetDateTime newLastUpdateTime = link.getLastUpdateTime();

            for (var answer: response.items()) {
                if (answer.creationDate().isAfter(link.getLastUpdateTime())) {
                    addNewUpdateDescription(updateDescriptions, NEW_ANSWER_MESSAGE, answer.owner().name());
                    newLastUpdateTime = getLatestUpdateTime(newLastUpdateTime, answer.creationDate());

                } else if (answer.lastActivityDate().isAfter(link.getLastUpdateTime())) {
                    addNewUpdateDescription(updateDescriptions, UPDATE_ANSWER_MESSAGE, answer.owner().name());
                    newLastUpdateTime = getLatestUpdateTime(newLastUpdateTime, answer.creationDate());
                }
            }

            if (!updateDescriptions.isEmpty()) {
                return Optional.of(new Update(updateDescriptions.toString(), newLastUpdateTime));
            }
        }

        return Optional.empty();
    }

    public void addNewUpdateDescription(StringBuilder descriptions, String message, String author) {
        descriptions.append(message.formatted(author)).append("\n");
    }

    public OffsetDateTime getLatestUpdateTime(OffsetDateTime oldDateTime, OffsetDateTime newDateTime) {
        return newDateTime.isAfter(oldDateTime) ? newDateTime : oldDateTime;
    }
}
