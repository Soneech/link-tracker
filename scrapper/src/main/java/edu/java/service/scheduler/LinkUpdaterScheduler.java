package edu.java.service.scheduler;

import edu.java.client.BotClient;
import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.bot.response.LinkUpdateResponse;
import edu.java.service.updater.LinkUpdateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final BotClient botWebClient;

    private final LinkUpdateService linkUpdateService;

    @Value("${api.bot.update-properties.count}")
    private int updatesCount;

    @Value("${api.bot.update-properties.interval}")
    private long interval;

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        LOGGER.info("Getting updates...");

        List<LinkUpdateRequest> updates = linkUpdateService.fetchAllUpdates(updatesCount, interval);
        updates.forEach((update) -> {
            LinkUpdateResponse response = botWebClient.sendUpdate(update);
            LOGGER.info("Update for link with id %d: %s".formatted(update.id(), response.message()));
        });
    }
}
