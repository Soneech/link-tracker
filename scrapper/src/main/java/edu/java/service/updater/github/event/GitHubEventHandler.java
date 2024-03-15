package edu.java.service.updater.github.event;

import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public interface GitHubEventHandler {
    Optional<Update> fetchUpdate(Pair<String, String> userAndRepository, Link link);
}
