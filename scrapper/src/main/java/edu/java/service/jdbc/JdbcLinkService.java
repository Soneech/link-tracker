package edu.java.service.jdbc;

import edu.java.dao.jdbc.JdbcLinkDao;
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
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Link addLinkForUser(long chatId, Link link) throws TelegramChatNotFoundException,
        ResourceNotExistsException {

        chatService.checkThatChatExists(chatId);

        Optional<Link> foundLink = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl());
        if (foundLink.isPresent()) {
            throw new LinkAlreadyAddedException(chatId, link.getUrl());
        }

        foundLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        if (foundLink.isEmpty()) {
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(URI.create(link.getUrl()).getHost());
            updater.setLastUpdateTime(link);
        }
        return jdbcLinkDao.save(chatId, link);
    }

    @Override
    public Link deleteUserLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Link linkToDelete = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl())
            .orElseThrow(() ->
                new LinkNotFoundException(chatId, link.getUrl()));

        jdbcLinkDao.deleteChatLink(chatId, linkToDelete.getId());
        return linkToDelete;
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        return jdbcLinkDao.findAllOutdatedLinks(count, interval);
    }

    @Override
    public void setUpdateTime(Link link, OffsetDateTime lastUpdateTime) {
        jdbcLinkDao.setUpdateTime(link, lastUpdateTime);
    }

    @Override
    public void setCheckTime(Link link, OffsetDateTime lastCheckTime) {
        jdbcLinkDao.setCheckTime(link, lastCheckTime);
    }

    @Override
    public void deleteLink(Link link) {
        jdbcLinkDao.delete(link.getId());
    }
}
