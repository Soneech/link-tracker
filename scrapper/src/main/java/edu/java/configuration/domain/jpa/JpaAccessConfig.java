package edu.java.configuration.domain.jpa;

import edu.java.repository.JpaChatRepository;
import edu.java.repository.JpaLinkRepository;
import edu.java.service.LinkService;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.updater.LinkUpdatersHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    @Bean
    public LinkService jpaLinkService(JpaLinkRepository jpaLinkRepository, JpaChatService jpaChatService,
        LinkUpdatersHolder linkUpdatersHolder) {
        return new JpaLinkService(jpaLinkRepository, jpaChatService, linkUpdatersHolder);
    }

    @Bean
    public JpaChatService jpaChatService(JpaChatRepository jpaChatRepository) {
        return new JpaChatService(jpaChatRepository);
    }
}
