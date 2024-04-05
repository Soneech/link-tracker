package edu.java.bot.service.kafka;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueConsumer {

    private final UpdateService updateService;

    private static final Logger LOGGER = LogManager.getLogger();

    @KafkaListener(
        topics = "${app.kafka.link-updates-topic.name}",
        groupId = "${app.kafka.link-updates-topic.consumer-group-id}"
    )
    public void listen(LinkUpdateRequest request) {
        updateService.processUpdate(request);
        LOGGER.info("Received update from queue: %s".formatted(request));
    }
}
