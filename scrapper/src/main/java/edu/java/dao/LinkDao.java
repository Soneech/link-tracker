package edu.java.dao;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    List<Link> findChatLinks(long chatId);

    Optional<Link> findChatLinkByUrl(long chatId, String url);

    Link save(long chatId, Link link);

    void deleteChatLink(long chatId, long linkId);

    void delete(long linkId);

    Optional<Link> findLinkByUrl(String url);

    List<Link> findAllOutdatedLinks(int count, long interval);

    void setUpdateTime(Link link, OffsetDateTime lastUpdateTime);

    void setCheckTime(Link link, OffsetDateTime lastCheckTime);
}
