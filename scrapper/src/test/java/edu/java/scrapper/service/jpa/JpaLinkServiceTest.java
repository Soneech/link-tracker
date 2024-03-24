package edu.java.scrapper.service.jpa;

import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.service.jpa.JpaChatService;
import edu.java.service.jpa.JpaLinkService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JpaLinkServiceTest extends JpaServiceTest {

    @InjectMocks
    public JpaLinkService jpaLinkService;

    @Mock
    public JpaChatService jpaChatService;

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
        Chat chat = chats.getFirst();
        jpaLinkService.getChatLinks(chat.getId());
        verify(jpaLinkRepository).findAllByTgChatsId(chat.getId());
    }

    @Test
    public void testGetLinksForNonExistentChat() {
        Chat chat = chats.getFirst();
        doThrow(TelegramChatNotFoundException.class)
            .when(jpaChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jpaLinkService.getChatLinks(chat.getId()))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testAddLink() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        Link savedLink = Link.builder()
            .id(9819819L).url(link.getUrl())
            .build();
        when(jpaLinkRepository.save(link)).thenReturn(savedLink);

        jpaLinkService.addLinkForChat(chat.getId(), link);
        verify(linkUpdatersHolder).getUpdaterByDomain(GITHUB_DOMAIN);
        verify(gitHubLinkUpdater).checkThatLinkExists(link);
        verify(jpaLinkRepository).save(link);
        verify(jpaLinkRepository).saveLinkForChat(savedLink.getId(), chat.getId());
    }

    @Test
    public void testAddLinkOnlyForChat() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        Link savedLink = Link.builder()
            .id(9819819L).url(link.getUrl())
            .build();
        when(jpaLinkRepository.findByUrl(link.getUrl())).thenReturn(savedLink);

        jpaLinkService.addLinkForChat(chat.getId(), link);

        verify(jpaLinkRepository, never()).save(link);
        verify(jpaLinkRepository).saveLinkForChat(savedLink.getId(), chat.getId());
    }

    @Test
    public void testRepeatedAddingLink() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        when(jpaLinkRepository.existsLinkByTgChatsIdAndUrl(chat.getId(), link.getUrl()))
            .thenReturn(true);
        assertThatThrownBy(() -> jpaLinkService.addLinkForChat(chat.getId(), link))
            .isInstanceOf(LinkAlreadyAddedException.class);
    }

    @Test
    public void testAddLinkForNonExistentChat() {
        Chat chat = chats.getLast();
        Link link = links.getLast();

        doThrow(TelegramChatNotFoundException.class)
            .when(jpaChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jpaLinkService.addLinkForChat(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testDeleteLinkForChat() {
        Chat chat = chats.getLast();
        Link link = links.getFirst();
        Link linkToDelete = Link.builder()
            .id(8781989712L).url(link.getUrl())
            .build();

        when(jpaLinkRepository.findByTgChatsIdAndUrl(chat.getId(), link.getUrl()))
            .thenReturn(Optional.of(linkToDelete));
        when(jpaLinkRepository.existsLinkForAtLeastOneChat(linkToDelete.getId()))
            .thenReturn(true);
        Link actualLinkToDelete = jpaLinkService.deleteChatLink(chat.getId(), link);

        assertThat(actualLinkToDelete).isEqualTo(linkToDelete);
        verify(jpaLinkRepository).deleteForChat(chat.getId(), linkToDelete.getId());
        verify(jpaLinkRepository).existsLinkForAtLeastOneChat(linkToDelete.getId());
        verify(jpaLinkRepository, never()).delete(linkToDelete);
    }

    @Test
    public void testDeleteLink() {
        Chat chat = chats.getLast();
        Link link = links.getFirst();
        Link linkToDelete = Link.builder()
            .id(15978161L).url(link.getUrl())
            .build();

        when(jpaLinkRepository.findByTgChatsIdAndUrl(chat.getId(), link.getUrl()))
            .thenReturn(Optional.of(linkToDelete));

        Link actualLinkToDelete = jpaLinkService.deleteChatLink(chat.getId(), link);
        assertThat(actualLinkToDelete).isEqualTo(linkToDelete);
        verify(jpaLinkRepository).deleteForChat(chat.getId(), linkToDelete.getId());
        verify(jpaLinkRepository).existsLinkForAtLeastOneChat(linkToDelete.getId());
        verify(jpaLinkRepository).delete(linkToDelete);
    }

    @Test
    public void testDeleteLinkForNonExistentChat() {
        Chat chat = chats.getFirst();
        Link link = links.getFirst();

        doThrow(TelegramChatNotFoundException.class)
            .when(jpaChatService).checkThatChatExists(chat.getId());
        assertThatThrownBy(() -> jpaLinkService.deleteChatLink(chat.getId(), link))
            .isInstanceOf(TelegramChatNotFoundException.class);
    }

    @Test
    public void testDeleteNonTrackingLink() {
        Chat chat = chats.getLast();
        Link link = links.getFirst();
        assertThatThrownBy(() -> jpaLinkService.deleteChatLink(chat.getId(), link))
            .isInstanceOf(LinkNotFoundException.class);
    }

    @Test
    public void testFindAllOutdatedLinks() {
        int count = 10;
        int interval = 60;
        jpaLinkService.findAllOutdatedLinks(count, interval);
        verify(jpaLinkRepository).findAllOutdatedLinks(count, interval);
    }

    @Test
    public void testSetUpdateTime() {
        Link link = links.getLast();
        OffsetDateTime lastUpdateTime = OffsetDateTime.now();

        jpaLinkService.setUpdateTime(link, lastUpdateTime);
        verify(jpaLinkRepository).save(link);
    }

    @Test
    public void testSetCheckTime() {
        Link link = links.getFirst();
        OffsetDateTime lastCheckTime = OffsetDateTime.now();

        jpaLinkService.setCheckTime(link, lastCheckTime);
        verify(jpaLinkRepository).save(link);
    }
}
