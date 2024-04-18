package edu.java.scrapper.dao.jooq;

import edu.java.dao.jooq.JooqChatDao;
import edu.java.dao.jooq.JooqLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class JooqChatDaoTest extends IntegrationEnvironment {

    @Autowired
    private JooqChatDao jooqChatDao;

    @Autowired
    private JooqLinkDao jooqLinkDao;

    private static List<Chat> chats;

    private static Link link;

    @BeforeAll
    public static void setUp() {
        OffsetDateTime testDateTime =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);

        chats = List.of(
            new Chat(11111L, testDateTime),
            new Chat(22222L, testDateTime),
            new Chat(33333L, testDateTime),
            new Chat(44444L, testDateTime)
        );
        link = Link.builder()
            .url("https://github.com/NeilAlishev/SpringCourse")
            .lastCheckTime(testDateTime).lastUpdateTime(testDateTime)
            .build();
    }

    @Test
    public void testSaveChatAndFindById() {
        Chat chat = chats.getFirst();
        jooqChatDao.save(chat);

        Optional<Chat> foundChat = jooqChatDao.findById(chat.getId());
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getId()).isEqualTo(chat.getId());
        assertThat(foundChat.get().getRegisteredAt().toEpochSecond())
            .isEqualTo(chat.getRegisteredAt().toEpochSecond());
    }

    @Test
    public void testEmptyFindResult() {
        Optional<Chat> foundChat = jooqChatDao.findById(111181894897988787L);
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testDelete() {
        Chat chat = chats.get(1);

        jooqChatDao.save(chat);
        assertThat(jooqChatDao.exists(chat.getId())).isTrue();

        jooqChatDao.delete(chat.getId());
        assertThat(jooqChatDao.exists(chat.getId())).isFalse();
    }

    @Test
    public void testFindAllChatIdsWithLink() {
        Chat firstChat = chats.get(2);
        Chat secondChat = chats.get(3);

        jooqChatDao.save(firstChat);
        jooqChatDao.save(secondChat);

        jooqLinkDao.save(firstChat.getId(), link);
        Link savedLink = jooqLinkDao.save(secondChat.getId(), link);

        List<Long> expectedChatIds = List.of(firstChat.getId(), secondChat.getId());
        List<Long> actualChatIds = jooqChatDao.findAllChatIdsWithLink(savedLink.getId());

        assertThat(actualChatIds).isNotEmpty().isEqualTo(expectedChatIds);
    }
}
