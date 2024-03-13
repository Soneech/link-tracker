package edu.java.service.updater;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.Update;
import edu.java.model.Link;
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
    public Optional<Update> fetchUpdate(Link link) {
        long questionId = getQuestionId(link.getUrl());
        QuestionResponse response = stackOverflowWebClient.fetchQuestion(questionId);

        Optional<Update> update = Optional.empty();
        for (var item: response.items()) {
            if (item.lastActivityDate().isAfter(link.getLastUpdateTime())) {
                update = Optional.of(new Update(link.getId(), link.getUrl(),
                    "произошли изменения в вопросе.", item.lastActivityDate()));
                break;
            }
        }

        return update;
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
