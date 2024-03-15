package edu.java.service.updater;

import edu.java.dto.bot.request.LinkUpdateRequest;
import edu.java.dto.update.Update;
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
        List<Update> updates = new ArrayList<>();

        links.forEach((link) -> {
            String host = URI.create(link.getUrl()).getHost();
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(host);

            Optional<Update> update = updater.fetchUpdate(link);
            linkService.setCheckTime(link, OffsetDateTime.now(ZoneId.systemDefault()));

            update.ifPresent((u) -> {
                List<Long> chatIds = chatService.findAllChatsIdsWithLink(u.linkId());
                u.tgChatIds().addAll(chatIds);
                updates.add(u);

                if (u.httpStatus().equals(HttpStatus.OK)) {
                    linkService.setUpdateTime(link, u.updateTime());
                } else {
                    linkService.deleteLink(link);
                }
            });
        });

        return convertToLinkUpdateRequests(updates);
    }

    public List<LinkUpdateRequest> convertToLinkUpdateRequests(List<Update> updates) {
        List<LinkUpdateRequest> requests = new ArrayList<>();

        updates.forEach((update) -> {
            var linkUpdateRequest =
                new LinkUpdateRequest(update.linkId(), update.url(), update.description(), update.tgChatIds());
            requests.add(linkUpdateRequest);
        });

        return requests;
    }
}
