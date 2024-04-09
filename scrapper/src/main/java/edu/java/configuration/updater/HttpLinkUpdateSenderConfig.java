package edu.java.configuration.updater;

import edu.java.client.BotClient;
import edu.java.service.updater.bot.HttpLinkUpdateSender;
import edu.java.service.updater.bot.LinkUpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class HttpLinkUpdateSenderConfig {

    @Bean
    public LinkUpdateSender httpLinkUpdateSender(BotClient botWebClient) {
        return new HttpLinkUpdateSender(botWebClient);
    }
}
