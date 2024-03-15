package edu.java.service.updater.stackoverflow;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.model.Link;
import edu.java.service.updater.LinkUpdater;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private String supportDomain = "stackoverflow.com";

    private final StackOverflowClient stackOverflowWebClient;

    private final int idIndex = 3;

    @Override
    public String getSupportDomain() {
        return supportDomain;
    }

    @Override
    public Optional<LinkUpdates> fetchUpdates(Link link) {
        long questionId = getQuestionId(link.getUrl());

        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);

        for (var item: response.items()) {
            if (item.lastActivityDate().isAfter(link.getLastUpdateTime())) {
                // TODO
            }
        }
        return Optional.of(new LinkUpdates());
    }

    @Override
    public void setLastUpdateTime(Link link) {
        long questionId = getQuestionId(link.getUrl());
        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);
        link.setLastUpdateTime(response.items().getLast().lastActivityDate());
    }

    private long getQuestionId(String url) {
        String[] urlParts = url.split("/+");
        return Long.parseLong(urlParts[idIndex]);
    }
}
