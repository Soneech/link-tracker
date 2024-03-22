package edu.java.service.multidao;

import edu.java.dao.LinkDao;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.ResourceNotExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.service.LinkService;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.LinkUpdatersHolder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class MultiDaoLinkService implements LinkService {
    private final LinkDao linkDao; // jooq или jdbc

    private final MultiDaoChatService chatService;

    private final LinkUpdatersHolder linkUpdatersHolder;

    @Override
    public List<Link> getChatLinks(long chatId) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);
        return linkDao.findChatLinks(chatId);
    }

    @Override
    @Transactional
    public Link addLinkForChat(long chatId, Link link) throws TelegramChatNotFoundException,
        ResourceNotExistsException {

        chatService.checkThatChatExists(chatId);

        if (linkDao.existsForChat(link.getUrl(), chatId)) {
            throw new LinkAlreadyAddedException(chatId, link.getUrl());
        }

        if (!linkDao.exists(link.getUrl())) {
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(URI.create(link.getUrl()).getHost());
            updater.checkThatLinkExists(link);
            link.setLastUpdateTime(OffsetDateTime.now(ZoneId.systemDefault()));
        }
        return linkDao.save(chatId, link);
    }

    @Override
    public Link deleteChatLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Link linkToDelete = linkDao.findChatLinkByUrl(chatId, link.getUrl())
            .orElseThrow(() ->
                new LinkNotFoundException(chatId, link.getUrl()));

        linkDao.deleteChatLink(chatId, linkToDelete.getId());
        return linkToDelete;
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        return linkDao.findAllOutdatedLinks(count, interval);
    }

    @Override
    public void setUpdateTime(Link link, OffsetDateTime lastUpdateTime) {
        linkDao.setUpdateTime(link, lastUpdateTime);
    }

    @Override
    public void setCheckTime(Link link, OffsetDateTime lastCheckTime) {
        linkDao.setCheckTime(link, lastCheckTime);
    }

    @Override
    public void deleteLink(Link link) {
        linkDao.delete(link.getId());
    }
}
