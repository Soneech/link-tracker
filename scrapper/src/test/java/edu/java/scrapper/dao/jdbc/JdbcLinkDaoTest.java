package edu.java.scrapper.dao.jdbc;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class JdbcLinkDaoTest extends JdbcDaoTest {
    private static JdbcLinkDao jdbcLinkDao;

    private static JdbcChatDao jdbcChatDao;

    private static Chat firstChat;

    private static Chat secondChat;

    private static List<Link> links;

    @BeforeAll
    public static void setUp() {
        jdbcLinkDao = new JdbcLinkDao(jdbcTemplate);
        jdbcChatDao = new JdbcChatDao(jdbcTemplate);

        OffsetDateTime createdAt =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);
        firstChat = new Chat(888888L, createdAt);
        secondChat = new Chat(77777L, createdAt);
        jdbcChatDao.save(firstChat);
        jdbcChatDao.save(secondChat);

        links = List.of(
            new Link("https://github.com/Soneech/link-tracker"),
            new Link("https://github.com/sanyarnd/java-course-2023-backend-template"),
            new Link("https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock"),
            new Link("https://github.com/Soneech/polls-client"),
            new Link("https://github.com/Soneech/polls-server"),
            new Link("https://github.com/ivannikolaev/java_h2"),
            new Link("https://stackoverflow.com/questions/74669679/return-boolean-if-postgresql-match-is-found")
        );
    }

    @Test
    public void testSaveAndGetLinks() {
        jdbcLinkDao.save(firstChat.getId(), links.getFirst());
        List<Link> firstChatLinks = jdbcLinkDao.findChatLinks(firstChat.getId());
        assertThat(firstChatLinks).isNotEmpty();
    }

    @Test
    public void testGetLinkByUrl() {
        Link link = links.get(1);
        jdbcLinkDao.save(firstChat.getId(), link);
        Optional<Link> chatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), link.getUrl());

        assertThat(chatLink).isPresent();
        assertThat(chatLink.get().getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testSaveLinkWithManyToManyRelationship() {
        Link link = links.get(2);
        jdbcLinkDao.save(firstChat.getId(), link);
        jdbcLinkDao.save(secondChat.getId(), link);

        Optional<Link> firstChatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), link.getUrl());
        Optional<Link> secondChatLink = jdbcLinkDao.findChatLinkByUrl(secondChat.getId(), link.getUrl());

        assertThat(firstChatLink).isPresent();
        assertThat(secondChatLink).isPresent();
        assertThat(firstChatLink.get()).isEqualTo(secondChatLink.get());
    }

    @Test
    public void testDeleteLink() {
        Link link = links.get(3);
        jdbcLinkDao.save(firstChat.getId(), link);

        Optional<Link> chatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), link.getUrl());
        assertThat(chatLink).isPresent();

        jdbcLinkDao.deleteChatLink(firstChat.getId(), chatLink.get().getId());
        chatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), link.getUrl());
        assertThat(chatLink).isEmpty();
    }

    @Test
    public void testDeleteLinkWithManyToManyRelationship() {
        Link link = links.get(4);
        jdbcLinkDao.save(secondChat.getId(), link);
        jdbcLinkDao.save(firstChat.getId(), link);

        Optional<Link> chatLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(chatLink).isPresent();

        jdbcLinkDao.deleteChatLink(firstChat.getId(), chatLink.get().getId());
        chatLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(chatLink).isPresent();

        jdbcLinkDao.deleteChatLink(secondChat.getId(), chatLink.get().getId());
        chatLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(chatLink).isEmpty();
    }

    @Test
    public void testFindAllOutdatedLinks() {
        Link firstLink = links.getFirst();
        Link secondLink = links.get(1);

        OffsetDateTime testDateTime = OffsetDateTime.now(); // тут нужно именно текущее время
        firstLink.setLastCheckTime(testDateTime);
        secondLink.setLastCheckTime(testDateTime);

        jdbcLinkDao.save(secondChat.getId(), firstLink);
        jdbcLinkDao.save(secondChat.getId(), secondLink);

        List<Link> foundLinks = jdbcLinkDao.findAllOutdatedLinks(2, 60);
        assertThat(foundLinks).isEmpty();
        foundLinks = jdbcLinkDao.findAllOutdatedLinks(2, 0);
        assertThat(foundLinks).isNotEmpty();
        assertThat(foundLinks).hasSize(2);
    }

    @Test
    public void testSetUpdateAndCheckTime() {
        Link link = links.get(5);
        jdbcLinkDao.save(secondChat.getId(), link);

        Optional<Link> chatLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(chatLink).isPresent();

        OffsetDateTime lastUpdateTime = OffsetDateTime.now();
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        jdbcLinkDao.setUpdateTime(chatLink.get(), lastUpdateTime);
        jdbcLinkDao.setCheckTime(chatLink.get(), lastCheckTime);
        chatLink = jdbcLinkDao.findLinkByUrl(link.getUrl());
        assertThat(chatLink).isPresent();

        assertThat(chatLink.get().getLastCheckTime().toEpochSecond()).isEqualTo(lastCheckTime.toEpochSecond());
        assertThat(chatLink.get().getLastUpdateTime().toEpochSecond()).isEqualTo(lastUpdateTime.toEpochSecond());
    }

    @Test
    public void testLinkExistence() {
        Link link = links.getLast();
        String exampleUrl = "https://example.com";

        jdbcLinkDao.save(firstChat.getId(), link);
        assertThat(jdbcLinkDao.exists(link.getUrl())).isTrue();
        assertThat(jdbcLinkDao.existsForChat(link.getUrl(), firstChat.getId())).isTrue();

        assertThat(jdbcLinkDao.exists(exampleUrl)).isFalse();
        assertThat(jdbcLinkDao.existsForChat(exampleUrl, firstChat.getId())).isFalse();
    }
}
