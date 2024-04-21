package edu.java.service.updater.bot;

import edu.java.client.BotClient;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class HttpLinkUpdateSender implements LinkUpdateSender {

    private final BotClient botWebClient;

    @Override
    public void send(List<LinkUpdateRequest> requests) {
        requests.forEach(request -> {
            LinkUpdateResponse response = botWebClient.sendUpdate(request);
            log.info("Send update with bot client; url: %s; response: %s"
                .formatted(request.url(), response.message()));
        });
    }
}
