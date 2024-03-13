package edu.java.service.jdbc;

import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.service.LinkService;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.LinkUpdatersHolder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkDao jdbcLinkDao;

    private final JdbcChatService chatService;

    private final LinkUpdatersHolder linkUpdatersHolder;

    @Override
    public List<Link> getUserLinks(long chatId) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);
        return jdbcLinkDao.findChatLinks(chatId);
    }

    @Override
    public Link addLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Optional<Link> chatLink = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl());
        if (chatLink.isPresent()) {
            throw new LinkAlreadyAddedException(chatId, link.getUrl());
        }

        LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(URI.create(link.getUrl()).getHost());
        updater.setLastUpdateTime(link);
        return jdbcLinkDao.save(chatId, link);
    }

    @Override
    public Link deleteLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Link linkToDelete = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl())
            .orElseThrow(() ->
                new LinkNotFoundException(chatId, link.getUrl()));

        jdbcLinkDao.delete(chatId, linkToDelete.getId());
        return linkToDelete;
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        return jdbcLinkDao.findAllOutdatedLinks(count, interval);
    }

    @Override
    public void setUpdateAndCheckTime(Link link, OffsetDateTime lastUpdateTime, OffsetDateTime lastCheckTime) {
        jdbcLinkDao.setUpdateAndCheckTime(link, lastUpdateTime, lastCheckTime);
    }
}
