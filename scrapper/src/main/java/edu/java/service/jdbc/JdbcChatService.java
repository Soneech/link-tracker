package edu.java.service.jdbc;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Chat;
import edu.java.service.ChatService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final JdbcChatDao jdbcChatDao;

    public void registerChat(Chat chat) {
        Optional<Chat> foundChat = jdbcChatDao.findById(chat.getId());
        if (foundChat.isPresent()) {
            throw new TelegramChatAlreadyExistsException(chat.getId());
        }
        jdbcChatDao.save(chat);
    }

    public void unregisterChat(long chatId) throws TelegramChatNotFoundException {
        checkThatChatExists(chatId);
        jdbcChatDao.delete(chatId);
    }

    public void checkThatChatExists(long chatId) {
        jdbcChatDao.findById(chatId)
            .orElseThrow(() -> new TelegramChatNotFoundException(chatId));
    }

    @Override
    public Optional<Chat> findChat(long chatId) {
        return jdbcChatDao.findById(chatId);
    }
}
