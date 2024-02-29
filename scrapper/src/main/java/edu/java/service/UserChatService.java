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
            throw new TelegramChatAlreadyExistsException("Чат с id %d уже зарегистирован.".formatted(chatId));
        }
        userChats.put(chatId, new UserChat(chatId, new ArrayList<>()));
    }

    public void removeChat(Long chatId) throws TelegramChatNotFoundException {
        Optional<UserChat> foundChat = findChat(chatId);
        if (foundChat.isPresent()) {
            userChats.remove(chatId);
        }
    }

    public Optional<UserChat> findChatOrNullable(Long chatId) {
        return Optional.ofNullable(userChats.get(chatId));
    }

    public Optional<UserChat> findChat(Long chatId) {
        Optional<UserChat> foundChat = Optional.ofNullable(userChats.get(chatId));
        if (foundChat.isEmpty()) {
            throw new TelegramChatNotFoundException("Чат с id %d не найден.".formatted(chatId));
        }
        return foundChat;
    }

    public List<Link> getUserLinks(Long chatId) throws TelegramChatNotFoundException {
        Optional<UserChat> foundChat = findChat(chatId);
        return foundChat.map(UserChat::getTrackingLinks).orElse(null);
    }

    public Link addLink(Long chatId, Link link) throws TelegramChatNotFoundException {
        Optional<UserChat> userChat = findChat(chatId);

        Optional<Link> foundLink = findLinkByURI(userChat.get(), link.getLink());
        if (foundLink.isPresent()) {
            throw new LinkAlreadyAddedException("Такая ссылка для чата c id %d уже добавлена".formatted(chatId));
        }

        link.setId(0L);  // пока нет бд...
        userChat.get().getTrackingLinks().add(link);
        return link;
    }

    public Optional<Link> findLinkByURI(UserChat chat, String link) {
        for (var currentLink: chat.getTrackingLinks()) {
            if (currentLink.getLink().equals(link)) {
                return Optional.of(currentLink);
            }
        }
        return Optional.empty();
    }

    public Link removeLink(Long chatId, Link link) throws TelegramChatNotFoundException {
        Optional<UserChat> userChat = findChat(chatId);

        Optional<Link> foundLink = findLinkByURI(userChat.get(), link.getLink());
        if (foundLink.isEmpty()) {
            throw new LinkNotFoundException("Ссылка %s для чата с id %d не найдена."
                .formatted(link.getLink(), chatId));
        }
        userChat.get().getTrackingLinks().remove(foundLink.get());
        return foundLink.get();
    }
}
