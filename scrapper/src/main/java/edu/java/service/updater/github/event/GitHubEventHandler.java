package edu.java.service.updater.github.event;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.update.Update;
import java.util.List;
import java.util.Optional;

public interface GitHubEventHandler {
    Optional<Update> fetchUpdate(List<EventResponse> newEvents);

    String getEventTypeName();
}
