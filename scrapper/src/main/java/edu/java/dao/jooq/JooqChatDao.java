package edu.java.dao.jooq;

import edu.java.dao.ChatDao;
import edu.java.model.Chat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import static edu.java.dao.jooq.generated.Tables.CHAT;
import static edu.java.dao.jooq.generated.Tables.CHAT_LINK;

@Component
@Primary
@RequiredArgsConstructor
public class JooqChatDao implements ChatDao {

    private final DSLContext dslContext;

    @Override
    public Optional<Chat> findById(long chatId) {
        return Optional.ofNullable(
            dslContext
                .selectFrom(CHAT).where(CHAT.ID.eq(chatId))
                .fetchOneInto(Chat.class));
    }

    @Override
    public void save(Chat chat) {
        dslContext
            .insertInto(CHAT, CHAT.ID, CHAT.REGISTERED_AT)
            .values(chat.getId(), chat.getRegisteredAt())
            .execute();
    }

    @Override
    public void delete(long chatId) {
        dslContext
            .deleteFrom(CHAT)
            .where(CHAT.ID.eq(chatId)).execute();
    }

    @Override
    public List<Long> findAllChatIdsWithLink(long linkId) {
        return dslContext
            .select(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);
    }

    @Override
    public Boolean exists(long chatId) {
        return dslContext
            .fetchExists(
                dslContext.select(CHAT.ID).from(CHAT).where(CHAT.ID.eq(chatId))
            );
    }
}
