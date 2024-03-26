package edu.java.scrapper.service.jpa;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.JpaChatRepository;
import edu.java.repository.JpaLinkRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;

public abstract class JpaServiceTest {
    @Mock
    protected JpaChatRepository jpaChatRepository;

    @Mock
    protected JpaLinkRepository jpaLinkRepository;

    protected static List<Chat> chats;

    protected static List<Link> links;

    @BeforeAll
    public static void setUp() {
        OffsetDateTime testDateTime =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);

        chats = List.of(
            new Chat(456454984L, testDateTime),
            new Chat(981919819L, testDateTime)
        );
        links = List.of(
            Link.builder().url("https://github.com/maximal/http-267")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build(),
            Link.builder().url("https://github.com/pagekit/vue-resource")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build()
        );
    }
}
