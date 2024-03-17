package edu.java.scrapper.service;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.service.multidao.MultiDaoChatService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiDaoChatServiceTest extends MultiDaoServiceTest {

    @InjectMocks
    private MultiDaoChatService multiDaoChatService;

    @Test
    public void testRegistration() {
        multiDaoChatService.registerChat(chat);
        verify(chatDao).save(chat);
    }

    @Test
    public void testRepeatedRegistration() {
        when(chatDao.findById(chat.getId())).thenReturn(Optional.of(chat));

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> multiDaoChatService.registerChat(chat));
    }

    @Test
    public void testUnregisterUser() {
        when(chatDao.findById(chat.getId())).thenReturn(Optional.of(chat));
        multiDaoChatService.unregisterChat(chat.getId());

        verify(chatDao).delete(chat.getId());
    }

    @Test
    public void testUnregisterNonExistentChat() {
        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> multiDaoChatService.unregisterChat(chat.getId()));
    }

    @Test
    public void testFindChat() {
        multiDaoChatService.findChat(chat.getId());
        verify(chatDao).findById(chat.getId());
    }

    @Test
    public void testFindAllChatsIdsWithLink() {
        long testLinkId = 123;
        when(chatDao.findAllChatIdsWithLink(testLinkId)).thenReturn(List.of(chat.getId()));

        List<Long> chatIds = multiDaoChatService.findAllChatsIdsWithLink(testLinkId);
        assertThat(chatIds).hasSize(1);
        assertThat(chatIds.getFirst()).isEqualTo(chat.getId());
    }
}
