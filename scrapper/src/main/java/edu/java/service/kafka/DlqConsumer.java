package edu.java.service.kafka;

import edu.java.dto.bot.request.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class DlqConsumer {

    @KafkaListener(
        topics = "${app.kafka.dlq-topic.name}",
        groupId = "${app.kafka.dlq-topic.consumer-group-id}"
    )
    public void listen(LinkUpdateRequest request) {
        log.error("Invalid update message that bot cannot process: %s".formatted(request));
    }
}
