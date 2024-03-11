package edu.java.scrapper.service;

import edu.java.dao.jdbc.JdbcLinkDao;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.service.jdbc.JdbcChatService;
import edu.java.service.jdbc.JdbcLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcLinkServiceTest extends JdbcServiceTest {
    private static JdbcLinkService jdbcLinkService;

    private static JdbcChatService jdbcChatService;

    @BeforeEach
    public void linkServiceSetUp() {
        jdbcChatService = mock(JdbcChatService.class);
        jdbcLinkDao = mock(JdbcLinkDao.class);
        jdbcLinkService = new JdbcLinkService(jdbcLinkDao, jdbcChatService);
    }

    @Test
    public void testGettingUserLinks() {
        jdbcLinkService.getUserLinks(chat.getId());
        verify(jdbcLinkDao).findChatLinks(chat.getId());
    }

    @Test
    public void testGettingLinksForNonExistentUser() {
        doThrow(TelegramChatNotFoundException.class)
            .when(jdbcChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jdbcLinkService.getUserLinks(chat.getId()))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testAddingLink() {
        jdbcLinkService.addLink(chat.getId(), link);
        verify(jdbcLinkDao).save(chat.getId(), link);
    }

    @Test
    public void testRepeatedAddingLink() {
        when(jdbcLinkDao.findChatLinkByUrl(chat.getId(), link.getUrl()))
            .thenReturn(Optional.of(link));
        assertThatThrownBy(() -> jdbcLinkService.addLink(chat.getId(), link))
            .isInstanceOf(LinkAlreadyAddedException.class);
    }

    @Test
    public void testAddingLinkForNonExistentUser() {
        doThrow(TelegramChatNotFoundException.class)
            .when(jdbcChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jdbcLinkService.addLink(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testDeletingLink() {
        Link testLink = link;
        testLink.setId(123L);

        when(jdbcLinkDao.findChatLinkByUrl(chat.getId(), testLink.getUrl()))
            .thenReturn(Optional.of(testLink));
        Link linkToDelete = jdbcLinkService.deleteLink(chat.getId(), testLink);

        assertThat(linkToDelete).isEqualTo(testLink);
        verify(jdbcLinkDao).delete(chat.getId(), linkToDelete.getId());

    }

    @Test
    public void testDeletingNonTrackingLink() {
        assertThatThrownBy(() -> jdbcLinkService.deleteLink(chat.getId(), link))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    public void testDeletingLinkForNonExistentUser() {
        doThrow(TelegramChatNotFoundException.class)
            .when(jdbcChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jdbcLinkService.deleteLink(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }
}
