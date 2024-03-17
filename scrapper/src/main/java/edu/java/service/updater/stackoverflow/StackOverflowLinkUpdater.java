package edu.java.service.updater.stackoverflow;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.stackoverflow.QuestionNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.stackoverflow.event.StackOverflowEventHandler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private final String supportDomain = "stackoverflow.com";

    private final StackOverflowClient stackOverflowWebClient;

    private final int idIndex = 3;

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<StackOverflowEventHandler> eventHandlers;

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<LinkUpdates> fetchUpdates(Link link) {
        long questionId = getQuestionId(link.getUrl());
        LinkUpdates linkUpdates = new LinkUpdates(link.getId(), link.getUrl(), HttpStatus.OK,
            link.getLastUpdateTime(), new ArrayList<>(), new ArrayList<>());

        // пока проверка на существование такая, по другим эндпоинам ответы ни о чём не говорящие
        try {
            checkThatLinkExists(link);
        } catch (QuestionNotExistsException exception) {
            linkUpdates.setHttpStatus(HttpStatus.GONE);
            linkUpdates.getUpdates().add(
                new Update(
                    "Вопрос больше не существует :(\nСсылка будет удалена",
                    OffsetDateTime.now())
            );
            return Optional.of(linkUpdates);
        }

        eventHandlers.forEach(handler -> {
            Optional<Update> update = handler.fetchUpdate(questionId, link);
            update.ifPresent(u -> addUpdate(linkUpdates, u));
        });

        if (linkUpdates.getUpdates().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(linkUpdates);
    }

    @Override
    public void checkThatLinkExists(Link link) {
        long questionId = getQuestionId(link.getUrl());
        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);
        LOGGER.info("Checks question: %s".formatted(response));

        if (response.items().isEmpty()) {
            throw new QuestionNotExistsException(questionId);
        }
    }

    private long getQuestionId(String url) {
        String[] urlParts = url.split("/+");
        return Long.parseLong(urlParts[idIndex]);
    }
}
