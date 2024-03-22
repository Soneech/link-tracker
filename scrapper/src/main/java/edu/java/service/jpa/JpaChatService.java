package edu.java.service.jpa;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Chat;
import edu.java.repository.JpaChatRepository;
import edu.java.service.ChatService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JpaChatService implements ChatService {

    private final JpaChatRepository jpaChatRepository;

    @Override
    public void registerChat(Chat chat) {
        if (jpaChatRepository.existsById(chat.getId())) {
            throw new TelegramChatAlreadyExistsException(chat.getId());
        }
        jpaChatRepository.save(chat);
    }

    @Override
    public void unregisterChat(long chatId) {
        checkThatChatExists(chatId);
        jpaChatRepository.deleteById(chatId);
    }

    @Override
    public void checkThatChatExists(long chatId) {
        if (!jpaChatRepository.existsById(chatId)) {
            throw new TelegramChatNotFoundException(chatId);
        }
    }

    @Override
    public Optional<Chat> findChat(long chatId) {
        return jpaChatRepository.findById(chatId);
    }

    @Override
    public List<Long> findAllChatsIdsWithLink(long linkId) {
        return jpaChatRepository.findAllChatsIdsWithLink(linkId);
    }
}
