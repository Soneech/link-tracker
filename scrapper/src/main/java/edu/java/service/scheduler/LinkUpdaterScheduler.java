package edu.java.service.scheduler;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.service.updater.LinkUpdateService;
import edu.java.service.updater.bot.LinkUpdateSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkUpdaterScheduler {

    private final LinkUpdateService linkUpdateService;

    private final LinkUpdateSender linkUpdateSender;

    @Value("${api.bot.update-properties.count}")
    private int updatesCount;

    @Value("${api.bot.update-properties.interval}")
    private long interval;

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    public void update() {
        log.info("Getting updates...");
        List<LinkUpdateRequest> updates = linkUpdateService.fetchAllUpdates(updatesCount, interval);
        linkUpdateSender.send(updates);
    }
}
