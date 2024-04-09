package edu.java.service.updater.bot;

import edu.java.dto.bot.request.LinkUpdateRequest;
import java.util.List;

public interface LinkUpdateSender {
    void send(List<LinkUpdateRequest> requests);
}
