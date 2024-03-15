package edu.java.service;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    List<Link> getUserLinks(long chatId);

    Link addLink(long chatId, Link link);

    Link deleteLink(long chatId, Link link);

    List<Link> findAllOutdatedLinks(int count, long interval);

    void setUpdateAndCheckTime(Link link, OffsetDateTime lastUpdateTime, OffsetDateTime lastCheckTime);
}
