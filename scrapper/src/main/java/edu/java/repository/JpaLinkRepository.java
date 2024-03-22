package edu.java.repository;

import edu.java.model.Link;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    Link findByUrl(String url);

    List<Link> findAllByTgChatsId(long chatId);

    boolean existsLinkByTgChatsIdAndUrl(long chatId, String url);

    Optional<Link> findByTgChatsIdAndUrl(long chatId, String url);

    @Modifying
    @Query(nativeQuery = true,
           value = "INSERT INTO chat_link (link_id, chat_id) VALUES (:linkId, :chatId)")
    void saveLinkForChat(long linkId, long chatId);

    @Modifying
    @Query(nativeQuery = true,
           value = "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId")
    void deleteForChat(long chatId, long linkId);

    @Query(nativeQuery = true,
           value = "SELECT EXISTS(SELECT chat_id FROM chat_link WHERE link_id = :linkId)")
    boolean existsLinkForAtLeastOneChat(long linkId);

    @Query(nativeQuery = true,
           value = """
                   SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time)) >= :interval OR
                                           last_update_time IS NULL ORDER BY last_check_time LIMIT :count
                   """)
    List<Link> findAllOutdatedLinks(int count, long interval);
}
