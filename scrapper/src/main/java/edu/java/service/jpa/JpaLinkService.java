package edu.java.service.jpa;

import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.ResourceNotExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.repository.JpaLinkRepository;
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
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository jpaLinkRepository;

    private final JpaChatService jpaChatService;

    private final LinkUpdatersHolder linkUpdatersHolder;

    @Override
    public List<Link> getChatLinks(long chatId) {
        jpaChatService.checkThatChatExists(chatId);
        return jpaLinkRepository.findAllByTgChatsId(chatId);
    }

    @Override
    @Transactional
    public Link addLinkForChat(long chatId, Link link) throws TelegramChatNotFoundException,
        ResourceNotExistsException {

        jpaChatService.checkThatChatExists(chatId);
        if (jpaLinkRepository.existsLinkByTgChatsIdAndUrl(chatId, link.getUrl())) {
            throw new LinkAlreadyAddedException(chatId, link.getUrl());
        }

        Link savedLink = jpaLinkRepository.findByUrl(link.getUrl());

        if (savedLink == null) {
            LinkUpdater updater = linkUpdatersHolder.getUpdaterByDomain(URI.create(link.getUrl()).getHost());
            updater.checkThatLinkExists(link);

            link.setLastCheckTime(OffsetDateTime.now(ZoneId.systemDefault()));
            link.setLastUpdateTime(OffsetDateTime.now(ZoneId.systemDefault()));
            savedLink = jpaLinkRepository.save(link);
        }

        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chatId);
        return savedLink;
    }

    @Override
    @Transactional
    public Link deleteChatLink(long chatId, Link link) throws TelegramChatNotFoundException {
        jpaChatService.checkThatChatExists(chatId);

        Link linkToDelete = jpaLinkRepository.findByTgChatsIdAndUrl(chatId, link.getUrl())
            .orElseThrow(() -> new LinkNotFoundException(chatId, link.getUrl()));

        jpaLinkRepository.deleteForChat(chatId, linkToDelete.getId());

        if (!jpaLinkRepository.existsLinkForAtLeastOneChat(linkToDelete.getId())) {
            jpaLinkRepository.delete(linkToDelete);
        }

        return linkToDelete;
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        return jpaLinkRepository.findAllOutdatedLinks(count, interval);
    }

    @Override
    public void setUpdateTime(Link link, OffsetDateTime lastUpdateTime) {
        link.setLastUpdateTime(lastUpdateTime);
        jpaLinkRepository.save(link);
    }

    @Override
    public void setCheckTime(Link link, OffsetDateTime lastCheckTime) {
        link.setLastCheckTime(lastCheckTime);
        jpaLinkRepository.save(link);
    }

    @Override
    public void deleteLink(Link link) {
        jpaLinkRepository.delete(link);
    }
}
