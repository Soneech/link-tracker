package edu.java.scrapper.service.jpa;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Chat;
import edu.java.service.jpa.JpaChatService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JpaChatServiceTest extends JpaServiceTest {

    @InjectMocks
    private JpaChatService jpaChatService;

    @Test
    public void testSuccessRegistration() {
        Chat chat = chats.getFirst();
        jpaChatService.registerChat(chat);
        verify(jpaChatRepository).save(chat);
    }

    @Test
    public void testRepeatedRegistration() {
        Chat chat = chats.getLast();
        when(jpaChatRepository.existsById(chat.getId())).thenReturn(true);

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> jpaChatService.registerChat(chat));
    }

    @Test
    public void testSuccessUnregisterUser() {
        Chat chat = chats.getFirst();
        when(jpaChatRepository.existsById(chat.getId())).thenReturn(true);

        jpaChatService.unregisterChat(chat.getId());
        verify(jpaChatRepository).deleteById(chat.getId());
    }

    @Test
    public void testUnregisterNonExistentChat() {
        Chat chat = chats.getLast();

        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> jpaChatService.unregisterChat(chat.getId()));
    }

    @Test
    public void testFindChat() {
        Chat chat = chats.getFirst();
        jpaChatService.findChat(chat.getId());
        verify(jpaChatRepository).findById(chat.getId());
    }

    @Test
    public void testFindAllChatsIdsWithLink() {
        long testLinkId = 8949847L;
        List<Long> chatIds = chats.stream().map(Chat::getId).toList();
        when(jpaChatService.findAllChatsIdsWithLink(testLinkId)).thenReturn(chatIds);

        List<Long> actualChatIds = jpaChatService.findAllChatsIdsWithLink(testLinkId);
        verify(jpaChatRepository).findAllChatsIdsWithLink(testLinkId);
        assertThat(actualChatIds).isNotEmpty().hasSize(2);
        assertThat(actualChatIds).isEqualTo(chatIds);
    }
}
