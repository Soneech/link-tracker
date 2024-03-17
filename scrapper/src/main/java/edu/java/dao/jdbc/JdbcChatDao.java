package edu.java.dao.jdbc;

import edu.java.dao.ChatDao;
import edu.java.model.Chat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JdbcChatDao implements ChatDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Chat> findById(long chatId) {
        return jdbcTemplate
            .query("SELECT * FROM chat WHERE id = ?", new BeanPropertyRowMapper<>(Chat.class), chatId)
            .stream().findAny();
    }

    @Override
    public void save(Chat chat) {
        jdbcTemplate.update("INSERT INTO chat (id, registered_at) VALUES (?, ?)",
            chat.getId(), chat.getRegisteredAt());
    }

    @Override
    public void delete(long chatId) {
        jdbcTemplate.update("DELETE FROM chat WHERE id = ?", chatId);
    }

    @Override
    public List<Long> findAllChatIdsWithLink(long linkId) {
        return
            jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
    }
}
