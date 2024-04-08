package edu.java.service.updater.bot;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.service.kafka.QueueProducer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class QueueLinkUpdateSender implements LinkUpdateSender {

    private final QueueProducer queueProducer;

    private final String topicName;

    @Override
    public void send(List<LinkUpdateRequest> requests) {
        requests.forEach(request -> {
            queueProducer.send(request, topicName);
            log.info("Send update to queue; link: %s".formatted(request.url()));
        });
    }
}
