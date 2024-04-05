package edu.java.bot.service.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueConsumer {

    private final UpdateService updateService;

    private final ApplicationConfig applicationConfig;

    private final DlqProducer dlqProducer;

    @KafkaListener(
        topics = "${app.kafka.link-updates-topic.name}",
        groupId = "${app.kafka.link-updates-topic.consumer-group-id}"
    )
    public void listen(LinkUpdateRequest request) {
        try {
            updateService.processUpdate(request);
            log.info("Received update from queue: %s".formatted(request));

        } catch (Exception exception) {
            log.error("Invalid update. Sending to DLQ...");
            dlqProducer.send(request, applicationConfig.kafka().dlqTopic().name());
        }
    }
}
