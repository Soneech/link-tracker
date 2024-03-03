package edu.java.service;

import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.model.UserChat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserChatService {
    private final Map<Long, UserChat> userChats = new HashMap<>();

    public void registerChat(Long chatId) {
        Optional<UserChat> foundChat = findChatOrNullable(chatId);
        if (foundChat.isPresent()) {
            throw new TelegramChatAlreadyExistsException(chatId);
        }
        userChats.put(chatId, new UserChat(chatId, new ArrayList<>()));
    }

    public void removeChat(Long chatId) throws TelegramChatNotFoundException {
        findChat(chatId);  // пока нет бд
        userChats.remove(chatId);
    }

    public Optional<UserChat> findChatOrNullable(Long chatId) {
        return Optional.ofNullable(userChats.get(chatId));
    }

    public UserChat findChat(Long chatId) {
        return Optional.ofNullable(userChats.get(chatId))
            .orElseThrow(() -> new TelegramChatNotFoundException(chatId));
    }

    public List<Link> getUserLinks(Long chatId) throws TelegramChatNotFoundException {
        UserChat foundChat = findChat(chatId);
        return foundChat.getTrackingLinks();
    }

    public Link addLink(Long chatId, Link link) throws TelegramChatNotFoundException {
        UserChat userChat = findChat(chatId);

        Optional<Link> foundLink = findLinkByURI(userChat, link.getLink());
        if (foundLink.isPresent()) {
            throw new LinkAlreadyAddedException(chatId, link.getLink());
        }

        link.setId(0L);  // тоже пока нет бд...
        userChat.getTrackingLinks().add(link);
        return link;
    }

    public Optional<Link> findLinkByURI(UserChat chat, String link) {
        return chat.getTrackingLinks().stream().filter(currentLink -> currentLink.getLink().equals(link)).findAny();
    }

    public Link removeLink(Long chatId, Link link) throws TelegramChatNotFoundException {
        UserChat userChat = findChat(chatId);

        Link foundLink = findLinkByURI(userChat, link.getLink())
            .orElseThrow(() ->
                new LinkNotFoundException(chatId, link.getLink()));

        userChat.getTrackingLinks().remove(foundLink);
        return foundLink;
    }
}
