package edu.java.service.updater;

import edu.java.dto.update.LinkUpdates;
import edu.java.model.Link;
import java.util.Optional;

public interface LinkUpdater {
    String getSupportDomain();

    void setLastUpdateTime(Link link);

    Optional<LinkUpdates> fetchUpdates(Link link);
}
