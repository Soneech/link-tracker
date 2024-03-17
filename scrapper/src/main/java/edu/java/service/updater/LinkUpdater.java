package edu.java.service.updater;

import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;

public interface LinkUpdater {
    String getSupportDomain();

    void checkThatLinkExists(Link link);

    Optional<LinkUpdates> fetchUpdates(Link link);

    default void addUpdate(LinkUpdates linkUpdates, Update update) {
        linkUpdates.getUpdates().add(update);
        if (update.updateTime().isAfter(linkUpdates.getLastUpdateTime())) {
            linkUpdates.setLastUpdateTime(update.updateTime());
        }
    }
}
