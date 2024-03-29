package edu.java.client;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import edu.java.exception.ResourceUnavailableException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

public interface BotClient extends HttpClient {
    LinkUpdateResponse sendUpdate(LinkUpdateRequest request);

    LinkUpdateResponse recoverSendUpdate(ResourceUnavailableException exception, LinkUpdateRequest request);

    LinkUpdateResponse recoverSendUpdate(WebClientRequestException exception, LinkUpdateRequest request);

    LinkUpdateResponse handleErrors(String message, LinkUpdateRequest request);
}
