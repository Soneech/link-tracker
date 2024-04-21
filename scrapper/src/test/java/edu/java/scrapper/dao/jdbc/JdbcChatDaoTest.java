package edu.java.scrapper.dao.jdbc;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcChatDaoTest extends JdbcDaoTest {
    private static JdbcChatDao jdbcChatDao;

    private static List<Chat> chats;

    @BeforeAll
    static void chatDaoSetUp() {
        jdbcChatDao = new JdbcChatDao(jdbcTemplate);

        OffsetDateTime createdAt =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);
        chats = List.of(
            new Chat(1111101101111L, createdAt),
            new Chat(2222222222002L, createdAt),
            new Chat(3333330330333L, createdAt),
            new Chat(4444444404404L, createdAt)
        );
    }

    @Test
    @Transactional
    public void testRegistration() {
        Chat chat = chats.getFirst();
        jdbcChatDao.save(chat);

        Optional<Chat> foundChat = jdbcChatDao.findById(chat.getId());
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getId()).isEqualTo(chat.getId());
    }

    @Test
    public void testEmptyFindResult() {
        long testId = 777L;

        Optional<Chat> foundChat = jdbcChatDao.findById(testId);
        assertThat(foundChat).isEmpty();
    }

    @Test
    @Transactional
    public void testChatRemoval() {
        Chat chat = chats.get(1);

        jdbcChatDao.save(chat);
        Optional<Chat> foundChat = jdbcChatDao.findById(chat.getId());
        assertThat(foundChat).isPresent();

        jdbcChatDao.delete(chat.getId());
        foundChat = jdbcChatDao.findById(chat.getId());
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testChatExistence() {
        Chat chat = chats.get(2);
        jdbcChatDao.save(chat);
        assertThat(jdbcChatDao.exists(chat.getId())).isTrue();
        assertThat(jdbcChatDao.exists(123456789L)).isFalse();
    }

    @Test
    @Transactional
    public void testFindChatsByLinkId() {
        Chat chat = chats.getLast();
        Link link = new Link("https://github.com/Soneech/polls-client");
        JdbcLinkDao jdbcLinkDao = new JdbcLinkDao(jdbcTemplate);

        jdbcChatDao.save(chat);
        jdbcLinkDao.save(chat.getId(), link);

        Optional<Link> foundLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(foundLink).isPresent();
        List<Long> foundChatIds = jdbcChatDao.findAllChatIdsWithLink(foundLink.get().getId());

        assertThat(foundChatIds).hasSize(1);
        assertThat(foundChatIds.getFirst()).isEqualTo(chat.getId());
    }
}
