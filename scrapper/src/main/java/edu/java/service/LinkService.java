package edu.java.service;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    List<Link> getUserLinks(long chatId);

    Link addLinkForUser(long chatId, Link link);

    Link deleteUserLink(long chatId, Link link);

    List<Link> findAllOutdatedLinks(int count, long interval);

    void setUpdateAndCheckTime(Link link, OffsetDateTime lastUpdateTime, OffsetDateTime lastCheckTime);

    void deleteLink(Link link);
}
