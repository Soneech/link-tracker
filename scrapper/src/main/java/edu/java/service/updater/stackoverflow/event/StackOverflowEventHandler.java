package edu.java.service.updater.stackoverflow.event;

import edu.java.dto.update.Update;
import edu.java.model.Link;
import java.util.Optional;

public interface StackOverflowEventHandler {
    Optional<Update> fetchUpdate(Long questionId, Link link);
}
