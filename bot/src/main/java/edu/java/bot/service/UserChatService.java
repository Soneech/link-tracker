package edu.java.bot.service;

import edu.java.bot.model.UserChat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserChatService {
    private final Map<Long, UserChat> userChats = new HashMap<>();

    public void register(UserChat userChat) {
        userChats.put(userChat.getChatId(), userChat);
    }

    public Optional<UserChat> findChat(Long chatId) {
        return Optional.ofNullable(userChats.get(chatId));
    }

    public List<String> getUserLinks(Long chatId) {
        Optional<UserChat> userChat = findChat(chatId);
        return userChat.map(UserChat::getTrackingLinks).orElse(null);
    }

    public boolean containsLink(Long chatId, String url) {
        Optional<UserChat> userChat = findChat(chatId);
        return userChat.map(chat -> chat.getTrackingLinks().contains(url)).orElse(false);
    }

    public void addLink(Long chatId, String link) {
        Optional<UserChat> userChat = findChat(chatId);
        userChat.ifPresent(chat -> chat.getTrackingLinks().add(link));
    }

    public void removeLink(Long chatId, String link) {
        Optional<UserChat> userChat = findChat(chatId);
        userChat.ifPresent(chat -> chat.getTrackingLinks().remove(link));
    }
}
