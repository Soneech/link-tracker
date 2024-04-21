package edu.java.scrapper.dao.jooq;

import edu.java.dao.jooq.JooqChatDao;
import edu.java.dao.jooq.JooqLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class JooqLinkDaoTest extends IntegrationEnvironment {

    private final JooqLinkDao jooqLinkDao;

    private final JooqChatDao jooqChatDao;

    private List<Chat> chats;

    private static List<Link> links;

    @Autowired
    public JooqLinkDaoTest(JooqLinkDao jooqLinkDao, JooqChatDao jooqChatDao) {
        this.jooqLinkDao = jooqLinkDao;
        this.jooqChatDao = jooqChatDao;

        OffsetDateTime createdAt =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);

        chats = List.of(
            new Chat(1597816518L, createdAt),
            new Chat(13068806808L, createdAt),
            new Chat(999999912L, createdAt)
        );

        chats.forEach(chat -> {
            if (!jooqChatDao.exists(chat.getId())) {
                jooqChatDao.save(chat);
            }
        });
    }

    @BeforeAll
    public static void setUp() {
        links = List.of(
            new Link("https://github.com/springframeworkguru/spring5-recipe-app"),
            new Link("https://github.com/kolorobot/spring-mvc-quickstart-archetype"),
            new Link("https://stackoverflow.com/questions/78349598/cloudfront-appears-to-be-blocking-post-request"),
            new Link("https://github.com/enhorse/java-interview"),
            new Link("https://github.com/spring-petclinic/spring-petclinic-rest"),
            new Link("https://github.com/danvega/security-demo"),
            new Link("https://stackoverflow.com/questions/78349578/mocking-electron-store-with-vitest")
        );
    }

    @Test
    public void testSaveAndFindByUrl() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();
        jooqLinkDao.save(chat.getId(), link);

        Optional<Link> foundLink = jooqLinkDao.findChatLinkByUrl(chat.getId(), link.getUrl());
        assertThat(foundLink).isPresent();
        assertThat(foundLink.get().getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testSaveAndGetChatLinks() {
        Chat chat = chats.getLast();
        List<Link> chatLinks = List.of(links.get(1), links.get(2));

        jooqLinkDao.save(chat.getId(), chatLinks.getFirst());
        jooqLinkDao.save(chat.getId(), chatLinks.getLast());

        List<Link> savedLinks = jooqLinkDao.findChatLinks(chat.getId());
        assertThat(savedLinks).isNotEmpty().hasSize(2);
        assertThat(savedLinks.getFirst().getUrl()).isEqualTo(chatLinks.getFirst().getUrl());
        assertThat(savedLinks.getLast().getUrl()).isEqualTo(chatLinks.getLast().getUrl());
    }

    @Test
    public void testSaveLinkWithManyToManyRelationShip() {
        Chat firstChat = chats.getFirst();
        Chat secondChat = chats.get(1);
        Link link = links.get(3);

        jooqLinkDao.save(firstChat.getId(), link);
        jooqLinkDao.save(secondChat.getId(), link);

        Optional<Link> firstChatLink = jooqLinkDao.findChatLinkByUrl(firstChat.getId(), link.getUrl());
        Optional<Link> secondChatLink = jooqLinkDao.findChatLinkByUrl(secondChat.getId(), link.getUrl());

        assertThat(firstChatLink).isPresent();
        assertThat(secondChatLink).isPresent();
        assertThat(firstChatLink.get()).isEqualTo(secondChatLink.get());
    }

    @Test
    public void testDelete() {
        Chat chat = chats.get(1);
        Link link = links.getFirst();

        jooqLinkDao.save(chat.getId(), link);
        Optional<Link> savedLink = jooqLinkDao.findChatLinkByUrl(chat.getId(), link.getUrl());
        assertThat(savedLink).isPresent();

        jooqLinkDao.deleteChatLink(chat.getId(), savedLink.get().getId());
        assertThat(jooqLinkDao.existsForChat(link.getUrl(), chat.getId())).isFalse();
    }

    @Test
    public void testDeleteLinkWithManyToManyRelationShip() {
        Chat firstChat = chats.getFirst();
        Chat secondChat = chats.get(1);
        Link link = links.get(4);

        jooqLinkDao.save(firstChat.getId(), link);
        jooqLinkDao.save(secondChat.getId(), link);

        Optional<Link> savedLink = jooqLinkDao.findLinkByUrl(link.getUrl());
        assertThat(savedLink).isPresent();

        jooqLinkDao.deleteChatLink(firstChat.getId(), savedLink.get().getId());
        assertThat(jooqLinkDao.exists(link.getUrl())).isTrue();

        jooqLinkDao.deleteChatLink(secondChat.getId(), savedLink.get().getId());
        assertThat(jooqLinkDao.exists(link.getUrl())).isFalse();
    }

    @Test
    public void testFindAllOutdatedLinks() {
        Chat chat = chats.get(1);
        Link firstLink = links.get(1);
        Link secondLink = links.get(2);

        OffsetDateTime testDateTime = OffsetDateTime.now();
        firstLink.setLastCheckTime(testDateTime);
        secondLink.setLastCheckTime(testDateTime);

        jooqLinkDao.save(chat.getId(), firstLink);
        jooqLinkDao.save(chat.getId(), secondLink);

        List<Link> foundLinks = jooqLinkDao.findAllOutdatedLinks(2, 60);
        assertThat(foundLinks).isEmpty();

        foundLinks = jooqLinkDao.findAllOutdatedLinks(2, 0);
        assertThat(foundLinks).isNotEmpty();
        assertThat(foundLinks).hasSize(2);
    }

    @Test
    public void testSetUpdateAndCheckTime() {
        Chat chat = chats.get(1);
        Link link = links.get(5);
        jooqLinkDao.save(chat.getId(), link);

        Optional<Link> savedLink = jooqLinkDao.findLinkByUrl(link.getUrl());
        assertThat(savedLink).isPresent();

        OffsetDateTime lastUpdateTime = OffsetDateTime.now();
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        jooqLinkDao.setUpdateTime(savedLink.get(), lastUpdateTime);
        jooqLinkDao.setCheckTime(savedLink.get(), lastCheckTime);
        savedLink = jooqLinkDao.findLinkByUrl(link.getUrl());
        assertThat(savedLink).isPresent();

        assertThat(savedLink.get().getLastCheckTime().toEpochSecond()).isEqualTo(lastCheckTime.toEpochSecond());
        assertThat(savedLink.get().getLastUpdateTime().toEpochSecond()).isEqualTo(lastUpdateTime.toEpochSecond());
    }

    @Test
    public void testLinkExistence() {
        Chat chat = chats.getFirst();
        Link link = links.get(6);
        String exampleUrl = "https://example.com";

        jooqLinkDao.save(chat.getId(), link);
        assertThat(jooqLinkDao.exists(link.getUrl())).isTrue();
        assertThat(jooqLinkDao.existsForChat(link.getUrl(), chat.getId())).isTrue();

        assertThat(jooqLinkDao.exists(exampleUrl)).isFalse();
        assertThat(jooqLinkDao.existsForChat(exampleUrl, chat.getId())).isFalse();
    }
}

