package edu.java.configuration.domain.jdbc;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.service.multidao.MultiDaoChatService;
import edu.java.service.multidao.MultiDaoLinkService;
import edu.java.service.updater.LinkUpdatersHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfig {

    @Bean
    public LinkService jdbcLinkService(JdbcLinkDao jdbcLinkDao, LinkUpdatersHolder linkUpdatersHolder,
        ChatService jdbcChatService) {
        return new MultiDaoLinkService(jdbcLinkDao, (MultiDaoChatService) jdbcChatService, linkUpdatersHolder);
    }

    @Bean
    public ChatService jdbcChatService(JdbcChatDao jdbcChatDao) {
        return new MultiDaoChatService(jdbcChatDao);
    }
}
