package edu.java.scrapper.service.multidao;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.service.multidao.MultiDaoChatService;
import java.util.List;
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
    public void testSuccessRegistration() {
        multiDaoChatService.registerChat(chat);
        verify(chatDao).save(chat);
    }

    @Test
    public void testRepeatedRegistration() {
        when(chatDao.exists(chat.getId())).thenReturn(true);

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> multiDaoChatService.registerChat(chat));
    }

    @Test
    public void testSuccessUnregisterChat() {
        when(chatDao.exists(chat.getId())).thenReturn(true);
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

        List<Long> actualChatIds = multiDaoChatService.findAllChatsIdsWithLink(testLinkId);
        verify(chatDao).findAllChatIdsWithLink(testLinkId);
        assertThat(actualChatIds).isNotEmpty().hasSize(1);
        assertThat(actualChatIds.getFirst()).isEqualTo(chat.getId());
    }
}
