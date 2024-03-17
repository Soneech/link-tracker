package edu.java.dao.jooq;

import edu.java.dao.LinkDao;
import edu.java.dao.jooq.generated.tables.records.LinkRecord;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import static edu.java.dao.jooq.generated.Tables.CHAT;
import static edu.java.dao.jooq.generated.Tables.CHAT_LINK;
import static edu.java.dao.jooq.generated.Tables.LINK;
import static org.jooq.impl.DSL.field;

@Component
@Primary
@RequiredArgsConstructor
public class JooqLinkDao implements LinkDao {

    private final DSLContext dslContext;

    @Override
    public List<Link> findChatLinks(long chatId) {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK).on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .join(CHAT).on(CHAT.ID.eq(CHAT_LINK.CHAT_ID))
            .where(CHAT.ID.eq(chatId))
            .fetchInto(Link.class);
    }

    @Override
    public Optional<Link> findChatLinkByUrl(long chatId, String url) {
        return Optional.ofNullable(
            dslContext
                .select(LINK.fields())
                .from(LINK)
                .join(CHAT_LINK).on(CHAT_LINK.CHAT_ID.eq(chatId))
                    .and(CHAT_LINK.LINK_ID.eq(LINK.ID))
                .where(LINK.URL.eq(url))
                .fetchOneInto(Link.class)
        );
    }

    @Override
    public Link save(long chatId, Link link) {
        Optional<Link> savedLink = findLinkByUrl(link.getUrl());

        if (savedLink.isEmpty()) {
            LinkRecord linkRecord = dslContext
                .insertInto(LINK, LINK.URL, LINK.LAST_UPDATE_TIME)
                .values(link.getUrl(), link.getLastUpdateTime()).returning().fetchOne();
            savedLink = Optional.of(linkRecord.into(Link.class));
        }

        dslContext
            .insertInto(CHAT_LINK, CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
            .values(chatId, savedLink.get().getId()).execute();
        return savedLink.get();
    }

    @Override
    public void deleteChatLink(long chatId, long linkId) {
        dslContext
            .deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
                .and(CHAT_LINK.LINK_ID.eq(linkId))
            .execute();

        List<Long> chatIdsWithThisLink =
            dslContext
                .select(CHAT_LINK.CHAT_ID).from(CHAT_LINK)
                .where(CHAT_LINK.LINK_ID.eq(linkId))
                .fetchInto(Long.class);

        if (chatIdsWithThisLink.isEmpty()) {
            delete(linkId);
        }
    }

    @Override
    public void delete(long linkId) {
        dslContext
            .deleteFrom(LINK).where(LINK.ID.eq(linkId))
            .execute();
    }

    @Override
    public Optional<Link> findLinkByUrl(String url) {
        return Optional.ofNullable(
            dslContext
                .select(LINK.fields())
                .from(LINK).where(LINK.URL.eq(url))
                .fetchOneInto(Link.class)
        );
    }

    @Override
    public List<Link> findAllOutdatedLinks(int count, long interval) {
        Field<Long> intervalFromLastCheckTime =
            field("EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time))", Long.class);

        return dslContext
            .select(LINK.fields()).from(LINK)
            .where(intervalFromLastCheckTime.greaterOrEqual(interval))
                .or(LINK.LAST_UPDATE_TIME.isNull())
            .limit(count)
            .fetchInto(Link.class);
    }

    @Override
    public void setUpdateTime(Link link, OffsetDateTime lastUpdateTime) {
        dslContext
            .update(LINK).set(LINK.LAST_UPDATE_TIME, lastUpdateTime)
            .where(LINK.ID.eq(link.getId())).execute();
    }

    @Override
    public void setCheckTime(Link link, OffsetDateTime lastCheckTime) {
        dslContext
            .update(LINK).set(LINK.LAST_CHECK_TIME, lastCheckTime)
            .where(LINK.ID.eq(link.getId())).execute();
    }
}
