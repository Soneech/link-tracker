package edu.java.configuration.domain.jooq;

import edu.java.dao.jooq.JooqChatDao;
import edu.java.dao.jooq.JooqLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.multidao.MultiDaoChatService;
import edu.java.service.multidao.MultiDaoLinkService;
import edu.java.service.updater.LinkUpdatersHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfig {

    @Bean
    public LinkService jooqLinkService(JooqLinkDao jooqLinkDao, ChatService jooqChatService,
        LinkUpdatersHolder linkUpdatersHolder) {
        return new MultiDaoLinkService(jooqLinkDao, (MultiDaoChatService) jooqChatService, linkUpdatersHolder);
    }

    @Bean
    public ChatService jooqChatService(JooqChatDao jooqChatDao) {
        return new MultiDaoChatService(jooqChatDao);
    }
}
