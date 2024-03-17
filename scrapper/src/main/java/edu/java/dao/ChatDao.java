package edu.java.dao;

import edu.java.model.Chat;
import java.util.List;
import java.util.Optional;

public interface ChatDao {
    Optional<Chat> findById(long chatId);

    void save(Chat chat);

    void delete(long chatId);

    List<Long> findAllChatIdsWithLink(long linkId);
}
