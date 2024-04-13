package edu.java.bot.service.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class QueueConsumer {

    private final UpdateService updateService;

    private final ApplicationConfig applicationConfig;

    private final DlqProducer dlqProducer;

    private final Counter processedUpdatesCounter;

    @KafkaListener(
        topics = "${app.kafka.link-updates-topic.name}",
        groupId = "${app.kafka.link-updates-topic.consumer-group-id}"
    )
    @Counted
    public void listen(LinkUpdateRequest request) {
        processedUpdatesCounter.increment();

        try {
            updateService.processUpdate(request);
            log.info("Received update from queue: %s".formatted(request));

        } catch (Exception exception) {
            log.error("Invalid update. Sending to DLQ...");
            dlqProducer.send(request, applicationConfig.kafka().dlqTopic().name());
        }
    }
}
