package edu.java.service.updater.bot;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.service.kafka.QueueProducer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public class QueueLinkUpdateSender implements LinkUpdateSender {

    private final QueueProducer queueProducer;

    private final String topicName;

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void send(List<LinkUpdateRequest> requests) {
        requests.forEach(request -> {
            queueProducer.send(request, topicName);
            LOGGER.info("Send update to queue; link: %s".formatted(request.url()));
        });
    }
}
