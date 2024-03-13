package edu.java.service;

import edu.java.model.Chat;
import java.util.List;
import java.util.Optional;

public interface ChatService {
    void registerChat(Chat chat);

    void unregisterChat(long chatId);

    void checkThatChatExists(long chatId);

    Optional<Chat> findChat(long chatId);

    List<Long> findAllChatsIdsWithLink(long linkId);
}
