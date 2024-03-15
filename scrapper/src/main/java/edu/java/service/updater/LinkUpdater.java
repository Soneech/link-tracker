package edu.java.service.updater;

import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;

public interface LinkUpdater {
    String getSupportDomain();

    void setLastUpdateTime(Link link);

    Optional<Update> fetchUpdate(Link link);
}
