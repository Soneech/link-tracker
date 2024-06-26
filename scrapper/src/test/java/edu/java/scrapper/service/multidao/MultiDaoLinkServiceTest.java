package edu.java.scrapper.service.multidao;

import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.service.multidao.MultiDaoChatService;
import edu.java.service.multidao.MultiDaoLinkService;
import edu.java.service.updater.LinkUpdatersHolder;
import edu.java.service.updater.github.GitHubLinkUpdater;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiDaoLinkServiceTest extends MultiDaoServiceTest {

    @InjectMocks
    private MultiDaoLinkService multiDaoLinkService;

    @Mock
    private MultiDaoChatService multiDaoChatService;

    @Mock
    private LinkUpdatersHolder linkUpdatersHolder;

    @Mock
    private GitHubLinkUpdater gitHubLinkUpdater;

    private static final String GITHUB_DOMAIN = "github.com";

    @BeforeEach
    public void linkServiceSetUp() {
        lenient().when(linkUpdatersHolder.getUpdaterByDomain(GITHUB_DOMAIN)).thenReturn(gitHubLinkUpdater);
    }

    @Test
    public void testGetChatLinks() {
        multiDaoLinkService.getChatLinks(chat.getId());
        verify(linkDao).findChatLinks(chat.getId());
    }

    @Test
    public void testGetLinksForNonExistentChat() {
        doThrow(TelegramChatNotFoundException.class)
            .when(multiDaoChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> multiDaoLinkService.getChatLinks(chat.getId()))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testAddLink() {
        multiDaoLinkService.addLinkForChat(chat.getId(), link);
        verify(linkDao).save(chat.getId(), link);
        verify(linkUpdatersHolder).getUpdaterByDomain(GITHUB_DOMAIN);
        verify(gitHubLinkUpdater).checkThatLinkExists(link);
    }

    @Test
    public void testRepeatedAddingLink() {
        when(linkDao.existsForChat(link.getUrl(), chat.getId()))
            .thenReturn(true);
        assertThatThrownBy(() -> multiDaoLinkService.addLinkForChat(chat.getId(), link))
            .isInstanceOf(LinkAlreadyAddedException.class);
    }

    @Test
    public void testAddLinkForNonExistentChat() {
        doThrow(TelegramChatNotFoundException.class)
            .when(multiDaoChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> multiDaoLinkService.addLinkForChat(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testDeleteLink() {
        Link testLink = link;
        testLink.setId(123L);

        when(linkDao.findChatLinkByUrl(chat.getId(), testLink.getUrl()))
            .thenReturn(Optional.of(testLink));
        Link linkToDelete = multiDaoLinkService.deleteChatLink(chat.getId(), testLink);

        assertThat(linkToDelete).isEqualTo(testLink);
        verify(linkDao).deleteChatLink(chat.getId(), linkToDelete.getId());

    }

    @Test
    public void testDeleteNonTrackingLink() {
        assertThatThrownBy(() -> multiDaoLinkService.deleteChatLink(chat.getId(), link))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    public void testDeleteLinkForNonExistentChat() {
        doThrow(TelegramChatNotFoundException.class)
            .when(multiDaoChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> multiDaoLinkService.deleteChatLink(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testFindAllOutdatedLinks() {
        int count = 10;
        int interval = 60;
        multiDaoLinkService.findAllOutdatedLinks(count, interval);
        verify(linkDao).findAllOutdatedLinks(count, interval);
    }

    @Test
    public void testSetUpdateAndCheckTime() {
        OffsetDateTime lastUpdateTime = OffsetDateTime.now();
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        multiDaoLinkService.setUpdateTime(link, lastUpdateTime);
        verify(linkDao).setUpdateTime(link, lastUpdateTime);

        multiDaoLinkService.setCheckTime(link, lastCheckTime);
        verify(linkDao).setCheckTime(link, lastCheckTime);
    }
}
