package edu.java.scrapper.dao;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.model.Chat;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcChatDaoTest extends IntegrationEnvironment {
    private static JdbcChatDao jdbcChatDao;

    @BeforeAll
    static void setUp() {
        jdbcChatDao = new JdbcChatDao(jdbcTemplate);
    }

    @Test
    public void testRegistration() {
        Chat testChat = new Chat(123456L, OffsetDateTime.now());
        jdbcChatDao.save(testChat);

        Optional<Chat> foundChat = jdbcChatDao.findById(testChat.getId());
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getId()).isEqualTo(testChat.getId());
    }

    @Test
    public void testEmptyFindResult() {
        long testId = 777L;

        Optional<Chat> foundChat = jdbcChatDao.findById(testId);
        assertThat(foundChat).isEmpty();
    }

    @Test
    public void testChatRemoval() {
        Chat testChat = new Chat(55555L, OffsetDateTime.now());

        jdbcChatDao.save(testChat);
        Optional<Chat> foundChat = jdbcChatDao.findById(testChat.getId());
        assertThat(foundChat).isPresent();

        jdbcChatDao.delete(testChat.getId());
        foundChat = jdbcChatDao.findById(testChat.getId());
        assertThat(foundChat).isEmpty();
    }
}
