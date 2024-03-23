package edu.java.scrapper.repository;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.JpaChatRepository;
import edu.java.repository.JpaLinkRepository;
import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaLinkRepositoryTest extends IntegrationEnvironment {

    private final JpaLinkRepository jpaLinkRepository;

    private final JpaChatRepository jpaChatRepository;

    private static List<Link> links;

    private static List<Chat> chats;

    @Autowired
    public JpaLinkRepositoryTest(JpaLinkRepository jpaLinkRepository, JpaChatRepository jpaChatRepository) {
        this.jpaLinkRepository = jpaLinkRepository;
        this.jpaChatRepository = jpaChatRepository;
    }

    @BeforeAll
    public static void testDataSetUp() {
        OffsetDateTime testDateTime = OffsetDateTime.now();
        links = List.of(
            Link.builder().url("https://github.com/maximal/http-267")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build(),
            Link.builder().url("https://stackoverflow.com/questions/78210424/wkwebview-js-injection")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build(),
            Link.builder().url("https://github.com/pagekit/vue-resource")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build(),
            Link.builder().url("https://stackoverflow.com/questions/39802264/jpa-how-to-persist-many-to-many-relation")
                .lastCheckTime(testDateTime).lastUpdateTime(testDateTime).build()
        );
        chats = List.of(
            new Chat(89898L, testDateTime),
            new Chat(98989L, testDateTime)
        );
    }

    @Test
    @Transactional
    public void testSaveAndFindChatLinks() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        jpaChatRepository.save(chat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chat.getId());

        List<Link> chatLinks = jpaLinkRepository.findAllByTgChatsId(chat.getId());
        assertThat(chatLinks).isNotEmpty().hasSize(1);
        assertThat(chatLinks.getFirst()).isEqualTo(savedLink);
    }

    @Test
    public void testSaveLink() {
        Link link = links.get(1);
        Link savedLink = jpaLinkRepository.save(link);

        assertThat(savedLink.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testFindLinkByUrl() {
        Link link = links.getLast();

        Link savedLink = jpaLinkRepository.save(link);
        Link foundLink = jpaLinkRepository.findByUrl(link.getUrl());

        assertThat(savedLink).isEqualTo(foundLink);
    }

    @Test
    @Transactional
    public void testSaveLinkWithManyToManyRelationShip() {
        Link link = links.get(2);
        Chat firstChat = chats.getFirst();
        Chat secondChat = chats.getLast();

        jpaChatRepository.save(firstChat);
        jpaChatRepository.save(secondChat);

        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), firstChat.getId());
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), secondChat.getId());

        Optional<Link> firstChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(firstChat.getId(), link.getUrl());
        Optional<Link> secondChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(secondChat.getId(), link.getUrl());

        assertThat(firstChatLink).isPresent();
        assertThat(secondChatLink).isPresent();
        assertThat(firstChatLink).isEqualTo(secondChatLink);
    }

    @Test
    public void testDeleteLinkForChat() {
        Link link = links.getFirst();
        Chat chat = chats.getFirst();

        jpaChatRepository.save(chat);
        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), chat.getId());

        Optional<Link> foundChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(chat.getId(), link.getUrl());
        assertThat(foundChatLink).isPresent();

        jpaLinkRepository.deleteForChat(chat.getId(), savedLink.getId());
        foundChatLink = jpaLinkRepository.findByTgChatsIdAndUrl(chat.getId(), link.getUrl());
        assertThat(foundChatLink).isEmpty();
    }

    @Test
    public void testFindAllOutdatedLinks() {
        Link firstLink = links.getFirst();
        Link secondLink = links.get(1);
        firstLink.setLastUpdateTime(OffsetDateTime.now());
        secondLink.setLastUpdateTime(OffsetDateTime.now());

        jpaLinkRepository.save(firstLink);
        jpaLinkRepository.save(secondLink);

        List<Link> foundLinks = jpaLinkRepository.findAllOutdatedLinks(2, 60);
        assertThat(foundLinks).isEmpty();
        foundLinks = jpaLinkRepository.findAllOutdatedLinks(2, 0);
        assertThat(foundLinks).isNotEmpty();
        assertThat(foundLinks).hasSize(2);
    }
}
