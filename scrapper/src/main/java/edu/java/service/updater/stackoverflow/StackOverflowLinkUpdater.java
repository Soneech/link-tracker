package edu.java.service.updater.stackoverflow;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.ResourceUnavailableException;
import edu.java.exception.stackoverflow.QuestionNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.stackoverflow.event.StackOverflowEventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {

    private final StackOverflowClient stackOverflowWebClient;

    private final List<StackOverflowEventHandler> eventHandlers;

    private static final String SUPPORT_DOMAIN = "stackoverflow.com";

    private static final String QUESTION_NOT_EXISTS_MESSAGE =
        "Вопрос больше не существует :(\nСсылка будет удалена";

    private static final int ID_INDEX = 3;

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Optional<LinkUpdates> fetchUpdates(Link link) {
        long questionId = getQuestionId(link.getUrl());
        LinkUpdates linkUpdates = LinkUpdates.builder()
            .linkId(link.getId())
            .url(link.getUrl())
            .httpStatus(HttpStatus.OK)
            .lastUpdateTime(link.getLastUpdateTime())
            .tgChatIds(new ArrayList<>()).updates(new ArrayList<>())
            .build();

        // пока проверка на существование такая, по другим эндпоинам ответы ни о чём не говорящие
        try {
            checkThatLinkExists(link);
        } catch (QuestionNotExistsException exception) {
            LOGGER.error(exception.getMessage());
            addResourceNotFoundUpdate(linkUpdates, QUESTION_NOT_EXISTS_MESSAGE);
            return Optional.of(linkUpdates);
        } catch (ResourceUnavailableException exception) {
            // TODO
        }

        eventHandlers.forEach(handler -> {
            Optional<Update> update = handler.fetchUpdate(questionId, link);
            update.ifPresent(u -> addUpdate(linkUpdates, u));
        });

        if (CollectionUtils.isEmpty(linkUpdates.getUpdates())) {
            return Optional.empty();
        }

        return Optional.of(linkUpdates);
    }

    @Override
    public String getSupportDomain() {
        return SUPPORT_DOMAIN;
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

    public long getQuestionId(String url) {
        String[] urlParts = url.split("/+");
        return Long.parseLong(urlParts[ID_INDEX]);
    }
}
