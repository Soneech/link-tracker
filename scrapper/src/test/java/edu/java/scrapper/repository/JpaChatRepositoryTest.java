package edu.java.scrapper.repository;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.JpaChatRepository;
import edu.java.repository.JpaLinkRepository;
import edu.java.scrapper.IntegrationEnvironment;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaChatRepositoryTest extends IntegrationEnvironment {

    private final JpaChatRepository jpaChatRepository;

    private final JpaLinkRepository jpaLinkRepository;

    private static List<Chat> chats;

    private static Link link;

    @Autowired
    public JpaChatRepositoryTest(JpaChatRepository jpaChatRepository, JpaLinkRepository jpaLinkRepository) {
        this.jpaChatRepository = jpaChatRepository;
        this.jpaLinkRepository = jpaLinkRepository;
    }

    @BeforeAll
    public static void testDataSetUp() {
        OffsetDateTime testDateTime =
            OffsetDateTime.of(2024, 3, 15, 13, 13, 0, 0, ZoneOffset.UTC);

        chats = List.of(
            new Chat(11111L, testDateTime),
            new Chat(22222L, testDateTime),
            new Chat(33333L, testDateTime),
            new Chat(44444L, testDateTime)
        );
        link = Link.builder()
            .url("https://github.com/Yankovsky/yandex-algos-training")
            .lastCheckTime(testDateTime).lastUpdateTime(testDateTime)
            .build();
    }

    @Test
    @Transactional
    public void testSaveChat() {
        Chat testChat = chats.getFirst();
        jpaChatRepository.save(testChat);
        Optional<Chat> savedChat = jpaChatRepository.findById(testChat.getId());

        assertThat(savedChat).isPresent();
        assertThat(savedChat.get()).isEqualTo(testChat);
    }

    @Test
    public void testEmptyFindResult() {
        Optional<Chat> foundChat = jpaChatRepository.findById(1984981819198484L);
        assertThat(foundChat).isEmpty();
    }

    @Test
    @Transactional
    public void testDeleteChat() {
        Chat testChat = chats.get(1);
        jpaChatRepository.save(testChat);
        assertThat(jpaChatRepository.existsById(testChat.getId())).isTrue();

        jpaChatRepository.deleteById(testChat.getId());
        assertThat(jpaChatRepository.existsById(testChat.getId())).isFalse();
    }

    @Test
    @Transactional
    public void testFindAllChatsIdsWithLink() {
        Chat firstChat = chats.get(2);
        Chat secondChat = chats.get(3);

        jpaChatRepository.save(firstChat);
        jpaChatRepository.save(secondChat);

        Link savedLink = jpaLinkRepository.save(link);
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), firstChat.getId());
        jpaLinkRepository.saveLinkForChat(savedLink.getId(), secondChat.getId());

        List<Long> chatsWithThisLink = jpaChatRepository.findAllChatsIdsWithLink(savedLink.getId());
        assertThat(chatsWithThisLink).isNotEmpty().hasSize(2);
        assertThat(chatsWithThisLink).contains(firstChat.getId());
        assertThat(chatsWithThisLink).contains(secondChat.getId());
    }
}
