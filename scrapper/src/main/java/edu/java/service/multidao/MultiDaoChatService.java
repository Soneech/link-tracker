package edu.java.service.multidao;

import edu.java.dao.ChatDao;
import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Chat;
import edu.java.service.ChatService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiDaoChatService implements ChatService {
    private final ChatDao chatDao;  // jooq или jdbc

    @Override
    public void registerChat(Chat chat) {
        Optional<Chat> foundChat = chatDao.findById(chat.getId());
        if (foundChat.isPresent()) {
            throw new TelegramChatAlreadyExistsException(chat.getId());
        }
        chatDao.save(chat);
    }

    @Override
    public void unregisterChat(long chatId) throws TelegramChatNotFoundException {
        checkThatChatExists(chatId);
        chatDao.delete(chatId);
    }

    @Override
    public void checkThatChatExists(long chatId) {
        chatDao.findById(chatId)
            .orElseThrow(() -> new TelegramChatNotFoundException(chatId));
    }

    @Override
    public Optional<Chat> findChat(long chatId) {
        return chatDao.findById(chatId);
    }

    @Override
    public List<Long> findAllChatsIdsWithLink(long linkId) {
        return chatDao.findAllChatIdsWithLink(linkId);
    }
}
