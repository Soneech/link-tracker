package edu.java.service.updater;

import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public interface LinkUpdater {
    String getSupportDomain();

    void checkThatLinkExists(Link link);

    Optional<LinkUpdates> fetchUpdates(Link link);

    default void addResourceNotFoundUpdate(LinkUpdates linkUpdates, String errorMessage) {
        linkUpdates.setHttpStatus(HttpStatus.GONE);
        linkUpdates.getUpdates().add(new Update(errorMessage, OffsetDateTime.now()));
    }

    default void addUpdate(LinkUpdates linkUpdates, Update update) {
        linkUpdates.getUpdates().add(update);
        if (update.updateTime().isAfter(linkUpdates.getLastUpdateTime())) {
            linkUpdates.setLastUpdateTime(update.updateTime());
        }
    }
}
