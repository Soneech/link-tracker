package edu.java.scrapper.service.multidao;

import edu.java.dao.ChatDao;
import edu.java.dao.LinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class MultiDaoServiceTest {
    protected static Chat chat;

    protected static Link link;

    @Mock
    protected LinkDao linkDao;

    @Mock
    protected ChatDao chatDao;

    @BeforeAll
    public static void setUp() {
        OffsetDateTime createdAt =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);
        link = new Link("https://github.com/pengrad/java-telegram-bot-api");
        chat = new Chat(29819818L, createdAt);
    }
}
