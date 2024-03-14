package edu.java.scrapper.service;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class JdbcServiceTest {
    protected static Chat chat;

    protected static Link link;

    @Mock
    protected JdbcLinkDao jdbcLinkDao;

    @Mock
    protected JdbcChatDao jdbcChatDao;

    @BeforeAll
    public static void setUp() {
        link = new Link("https://github.com/pengrad/java-telegram-bot-api");
        chat = new Chat(29819818L, OffsetDateTime.now());
    }
}
