package edu.java.dao.jdbc;

import edu.java.dao.LinkDao;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcLinkDao implements LinkDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Link> findChatLinks(long chatId) {
        return jdbcTemplate
            .query("""
                        SELECT l.* FROM link l JOIN chat_link cl ON l.id = cl.link_id JOIN chat c
                            ON c.id = cl.chat_id WHERE c.id=?
                    """,
                new BeanPropertyRowMapper<>(Link.class), chatId
            );
    }

    @Override
    public Optional<Link> findChatLinkByUrl(long chatId, String url) {
        return jdbcTemplate
            .query("""
                    SELECT l.* FROM link l JOIN chat_link cl ON cl.chat_id = ?
                        AND cl.link_id = l.id WHERE l.url = ?
                    """,
                new BeanPropertyRowMapper<>(Link.class), chatId, url
            ).stream().findAny();
    }

    @Override
    @Transactional
    public Link save(long chatId, Link link) {
        Optional<Link> savedLink = findLinkByUrl(link.getUrl());

        if (savedLink.isEmpty()) {
            jdbcTemplate.update("INSERT INTO link (url, last_update_time) VALUES (?, ?)",
                link.getUrl(), link.getLastUpdateTime()
            );
            savedLink = findLinkByUrl(link.getUrl());
        }

        jdbcTemplate
            .update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chatId, savedLink.get().getId());
        return savedLink.get();
    }

    @Override
    @Transactional
    public void deleteChatLink(long chatId, long linkId) {
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);

        List<Long> chatIdsWithThisLink =
            jdbcTemplate.queryForList("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, linkId);
        if (chatIdsWithThisLink.isEmpty()) {
            delete(linkId);
        }
    }

    @Override
    public void delete(long linkId) {
        jdbcTemplate.update("DELETE FROM link WHERE id = ?", linkId);
    }

    @Override
    public Optional<Link> findLinkByUrl(String url) {
        return jdbcTemplate
            .query("SELECT * FROM link WHERE url = ?", new BeanPropertyRowMapper<>(Link.class), url)
            .stream().findAny();
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        return jdbcTemplate
            .query("""
                    SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time)) >= ? OR
                        last_update_time IS NULL LIMIT ?
                    """,
                new BeanPropertyRowMapper<>(Link.class), interval, count
            );
    }

    @Override
    public Boolean exists(String url) {
        return jdbcTemplate
            .queryForObject("SELECT EXISTS (SELECT id FROM Link WHERE url = ?)", Boolean.class, url);
    }

    @Override
    public Boolean existsForChat(String url, long chatId) {
        return jdbcTemplate
            .queryForObject("""
                SELECT EXISTS (SELECT l.id FROM link l JOIN chat_link cl ON cl.chat_id = ?
                        AND cl.link_id = l.id WHERE l.url = ?)
                """, Boolean.class, chatId, url);
    }

    @Override
    public void setUpdateTime(Link link, OffsetDateTime lastUpdateTime) {
        jdbcTemplate
            .update("UPDATE Link SET last_update_time = ? WHERE id = ?", lastUpdateTime, link.getId());
    }

    @Override
    public void setCheckTime(Link link, OffsetDateTime lastCheckTime) {
        jdbcTemplate
            .update("UPDATE Link SET last_check_time = ? WHERE id = ?", lastCheckTime, link.getId());
    }
}
