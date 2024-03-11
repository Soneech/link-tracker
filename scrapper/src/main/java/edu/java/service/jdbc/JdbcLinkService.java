package edu.java.service.jdbc;

import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.service.LinkService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkDao jdbcLinkDao;


    private final JdbcChatService chatService;

    public List<Link> getUserLinks(long chatId) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);
        return jdbcLinkDao.findChatLinks(chatId);
    }

    public Link addLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Optional<Link> chatLink = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl());
        if (chatLink.isPresent()) {
            throw new LinkAlreadyAddedException(chatId, link.getUrl());
        }

        return jdbcLinkDao.save(chatId, link);
    }

    public Link deleteLink(long chatId, Link link) throws TelegramChatNotFoundException {
        chatService.checkThatChatExists(chatId);

        Link linkToDelete = jdbcLinkDao.findChatLinkByUrl(chatId, link.getUrl())
            .orElseThrow(() ->
                new LinkNotFoundException(chatId, link.getUrl()));

        jdbcLinkDao.delete(chatId, linkToDelete.getId());
        return linkToDelete;
    }
}
