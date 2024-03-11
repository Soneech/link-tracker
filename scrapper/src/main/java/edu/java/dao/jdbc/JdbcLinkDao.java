package edu.java.dao.jdbc;

import edu.java.model.Link;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class JdbcLinkDao {
    private final JdbcTemplate jdbcTemplate;

    public List<Link> findChatLinks(long chatId) {
        return jdbcTemplate
            .query(
                "SELECT l.* FROM link l JOIN chat_link cl ON l.id = cl.link_id JOIN chat c "
                    + "ON c.id = cl.chat_id WHERE c.id=?",
                new BeanPropertyRowMapper<>(Link.class), chatId);
    }

    public Optional<Link> findChatLinkByUrl(long chatId, String url) {
        return jdbcTemplate
            .query("SELECT l.* FROM link l JOIN chat_link cl ON cl.chat_id = ? "
                    + "AND cl.link_id = l.id WHERE l.url = ?",
                    new BeanPropertyRowMapper<>(Link.class), chatId, url)
            .stream().findAny();
    }

    public Link save(long chatId, Link link) {
        Link savedLink = findLinkByUrl(link.getUrl());

        if (savedLink == null) {
            jdbcTemplate.update("INSERT INTO link (url) VALUES (?)", link.getUrl());
            savedLink = findLinkByUrl(link.getUrl());
        }

        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chatId, savedLink.getId());
        return savedLink;
    }

    public void delete(long chatId, long linkId) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);

        List<Long> chatIdsWithThisLink =
            jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
        if (chatIdsWithThisLink.isEmpty()) {
            jdbcTemplate.update("DELETE FROM link WHERE id = ?", linkId);
        }
    }

    public Link findLinkByUrl(String url) {
        return jdbcTemplate
            .query("SELECT * FROM link WHERE url = ?", new BeanPropertyRowMapper<>(Link.class), url)
            .stream().findAny().orElse(null);
    }
}
