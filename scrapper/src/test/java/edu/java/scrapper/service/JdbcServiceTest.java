package edu.java.scrapper.service;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import org.junit.jupiter.api.BeforeAll;
import java.time.OffsetDateTime;

public abstract class JdbcServiceTest {
    protected static Chat chat;

    protected static Link link;

    protected JdbcChatDao jdbcChatDao;

    protected JdbcLinkDao jdbcLinkDao;

    @BeforeAll
    public static void setUp() {
        link = new Link("https://github.com/pengrad/java-telegram-bot-api");
        chat = new Chat(29819818L, OffsetDateTime.now());
    }
}
