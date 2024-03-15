package edu.java.service.updater;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.update.LinkUpdates;
import edu.java.model.Link;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final LinkUpdatersHolder linkUpdatersHolder;

    private final LinkService linkService;

    private final ChatService chatService;

    public List<LinkUpdateRequest> fetchAllUpdates(int updatesCount, long interval) {
        List<Link> links = linkService.findAllOutdatedLinks(updatesCount, interval);
        List<LinkUpdates> updates = new ArrayList<>();

        links.forEach((link) -> {
            String host = URI.create(link.getUrl()).getHost();
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(host);

            Optional<LinkUpdates> linkUpdates = updater.fetchUpdates(link);
            linkService.setCheckTime(link, OffsetDateTime.now(ZoneId.systemDefault()));

            linkUpdates.ifPresent(updatesContainer -> {
                List<Long> chatIds = chatService.findAllChatsIdsWithLink(link.getId());
                updatesContainer.getTgChatIds().addAll(chatIds);
                updates.add(updatesContainer);

                if (updatesContainer.getHttpStatus().equals(HttpStatus.OK)) {
                    linkService.setUpdateTime(link, updatesContainer.getLastUpdateTime());
                } else {
                    linkService.deleteLink(link);
                }
            });
        });

        return convertToLinkUpdateRequests(updates);
    }

    public List<LinkUpdateRequest> convertToLinkUpdateRequests(List<LinkUpdates> linkUpdates) {
        List<LinkUpdateRequest> requests = new ArrayList<>();

        linkUpdates.forEach((updateContainer) -> {
            List<String> descriptions = new ArrayList<>();
            updateContainer.getUpdates().forEach(update -> descriptions.add(update.description()));

            var linkUpdateRequest =
                new LinkUpdateRequest(updateContainer.getLinkId(), updateContainer.getUrl(),
                    descriptions, updateContainer.getTgChatIds());
            requests.add(linkUpdateRequest);
        });

        return requests;
    }
}
