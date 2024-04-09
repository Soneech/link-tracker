package edu.java.configuration.updater;

import edu.java.configuration.ApplicationConfig;
import edu.java.service.kafka.QueueProducer;
import edu.java.service.updater.bot.LinkUpdateSender;
import edu.java.service.updater.bot.QueueLinkUpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class QueueLinkUpdateSenderConfig {

    @Bean
    public LinkUpdateSender queueLinkUpdateSender(QueueProducer queueProducer,
                                                  ApplicationConfig applicationConfig) {
        return new QueueLinkUpdateSender(queueProducer, applicationConfig.kafka().linkUpdatesTopic().name());
    }
}
