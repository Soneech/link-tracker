package edu.java.repository;

import edu.java.model.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
    @Query(nativeQuery = true, value = "SELECT chat_id FROM chat_link WHERE link_id = :linkId")
    List<Long> findAllChatsIdsWithLink(long linkId);
}

