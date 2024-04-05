package edu.java.service.updater.bot;

import edu.java.client.BotClient;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public class HttpLinkUpdateSender implements LinkUpdateSender {

    private final BotClient botWebClient;

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void send(List<LinkUpdateRequest> requests) {
        requests.forEach(request -> {
            LinkUpdateResponse response = botWebClient.sendUpdate(request);
            LOGGER.info("Send update with bot client; link: %s; response: %s"
                .formatted(request.url(), response.message()));
        });
    }
}
