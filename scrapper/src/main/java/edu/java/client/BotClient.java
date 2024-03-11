package edu.java.client;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;

public interface BotClient extends HttpClient {
    LinkUpdateResponse sendUpdate(LinkUpdateRequest request);
}
