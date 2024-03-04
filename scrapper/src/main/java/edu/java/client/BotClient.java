package edu.java.client;

import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.dto.bot.LinkUpdateResponse;

public interface BotClient extends HttpClient {
    LinkUpdateResponse sendUpdate(LinkUpdateRequest request);
}
