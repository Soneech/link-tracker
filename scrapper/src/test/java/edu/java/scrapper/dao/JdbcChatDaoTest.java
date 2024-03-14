package edu.java.scrapper.dao;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcChatDaoTest extends IntegrationEnvironment {
    private static JdbcChatDao jdbcChatDao;

    @BeforeAll
    static void chatDaoSetUp() {
        jdbcChatDao = new JdbcChatDao(jdbcTemplate);
    }

    @Test
    @Transactional
    public void testRegistration() {
        Chat chat = new Chat(123456L, OffsetDateTime.now());
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
        Chat chat = new Chat(55555L, OffsetDateTime.now());

        jdbcChatDao.save(chat);
        Optional<Chat> foundChat = jdbcChatDao.findById(chat.getId());
        assertThat(foundChat).isPresent();

        jdbcChatDao.delete(chat.getId());
        foundChat = jdbcChatDao.findById(chat.getId());
        assertThat(foundChat).isEmpty();
    }

    @Test
    @Transactional
    public void testFindChatsByLinkId() {
        Chat chat = new Chat(55556L, OffsetDateTime.now());
        String url = "https://github.com/Soneech/link-tracker";
        JdbcLinkDao jdbcLinkDao = new JdbcLinkDao(jdbcTemplate);

        jdbcChatDao.save(chat);
        jdbcLinkDao.save(chat.getId(), new Link(url));

        Optional<Link> foundLink = jdbcLinkDao.findLinkByUrl(url);
        assertThat(foundLink).isPresent();
        List<Long> foundChatIds = jdbcChatDao.findAllChatIdsWithLink(foundLink.get().getId());

        assertThat(foundChatIds.size()).isOne();
        assertThat(foundChatIds.getFirst()).isEqualTo(chat.getId());
    }
}
