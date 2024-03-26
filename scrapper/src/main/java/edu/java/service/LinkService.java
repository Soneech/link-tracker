package edu.java.service;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    List<Link> getChatLinks(long chatId);

    Link addLinkForChat(long chatId, Link link);

    Link deleteChatLink(long chatId, Link link);

    List<Link> findAllOutdatedLinks(int count, long interval);

    void setUpdateTime(Link link, OffsetDateTime lastUpdateTime);

    void setCheckTime(Link link, OffsetDateTime lastCheckTime);

    void deleteLink(Link link);
}
