package edu.java.scrapper.dao;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.scrapper.IntegrationEnvironment;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcLinkDaoTest extends IntegrationEnvironment {
    private static JdbcLinkDao jdbcLinkDao;

    private static JdbcChatDao jdbcChatDao;

    private static Chat firstChat;

    private static Chat secondChat;

    private static List<Link> links;

    @BeforeAll
    public static void setUp() {
        jdbcLinkDao = new JdbcLinkDao(jdbcTemplate);
        jdbcChatDao = new JdbcChatDao(jdbcTemplate);

        firstChat = new Chat(888888L, "Username1");
        secondChat = new Chat(77777L, "Username2");
        jdbcChatDao.save(firstChat);
        jdbcChatDao.save(secondChat);

        links = List.of(
            new Link("https://github.com/Soneech/link-tracker"),
            new Link("https://github.com/sanyarnd/java-course-2023-backend-template"),
            new Link("https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock")
        );
    }

    @Test
    public void testSaveAndGetLinks() {
        jdbcLinkDao.save(firstChat.getId(), links.getFirst());
        jdbcLinkDao.save(firstChat.getId(), links.getLast());

        List<Link> firstChatLinks = jdbcLinkDao.findChatLinks(firstChat.getId());
        assertThat(firstChatLinks).isNotEmpty();
        assertThat(firstChatLinks.getFirst().getUrl()).isEqualTo(links.getFirst().getUrl());
        assertThat(firstChatLinks.getLast().getUrl()).isEqualTo(links.getLast().getUrl());

        Optional<Link> firstChatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), links.getFirst().getUrl());
        assertThat(firstChatLink).isPresent();
        assertThat(firstChatLink.get().getUrl()).isEqualTo(links.getFirst().getUrl());

        jdbcLinkDao.save(secondChat.getId(), links.getFirst());
        Optional<Link> secondChatLink = jdbcLinkDao.findChatLinkByUrl(secondChat.getId(), links.getFirst().getUrl());
        assertThat(secondChatLink).isPresent();
        assertThat(secondChatLink.get().getUrl()).isEqualTo(links.getFirst().getUrl());
        assertThat(secondChatLink.get()).isEqualTo(firstChatLink.get());
    }

    @Test
    public void testDeleteLinks() {
        jdbcLinkDao.save(firstChat.getId(), links.get(1));
        jdbcLinkDao.save(secondChat.getId(), links.get(1));
        String url = links.get(1).getUrl();

        Optional<Link> firstChatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), url);
        assertThat(firstChatLink).isPresent();

        jdbcLinkDao.delete(firstChat.getId(), firstChatLink.get().getId());
        firstChatLink = jdbcLinkDao.findChatLinkByUrl(firstChat.getId(), url);
        assertThat(firstChatLink).isEmpty();

        Link linkInDataBase = jdbcLinkDao.findLinkByUrl(url);
        assertThat(linkInDataBase).isNotNull();

        jdbcLinkDao.delete(secondChat.getId(), linkInDataBase.getId());
        linkInDataBase = jdbcLinkDao.findLinkByUrl(url);
        assertThat(linkInDataBase).isNull();
    }
}
