package edu.java.scrapper.service;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.service.jdbc.JdbcChatService;
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
public class JdbcChatServiceTest extends JdbcServiceTest {

    @InjectMocks
    private JdbcChatService jdbcChatService;

    @Test
    public void testRegistration() {
        jdbcChatService.registerChat(chat);
        verify(jdbcChatDao).save(chat);
    }

    @Test
    public void testRepeatedRegistration() {
        when(jdbcChatDao.findById(chat.getId())).thenReturn(Optional.of(chat));

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> jdbcChatService.registerChat(chat));
    }

    @Test
    public void testUnregisterUser() {
        when(jdbcChatDao.findById(chat.getId())).thenReturn(Optional.of(chat));
        jdbcChatService.unregisterChat(chat.getId());

        verify(jdbcChatDao).delete(chat.getId());
    }

    @Test
    public void testUnregisterNonExistentChat() {
        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> jdbcChatService.unregisterChat(chat.getId()));
    }

    @Test
    public void testFindChat() {
        jdbcChatService.findChat(chat.getId());
        verify(jdbcChatDao).findById(chat.getId());
    }

    @Test
    public void testFindAllChatsIdsWithLink() {
        long testLinkId = 123;
        when(jdbcChatDao.findAllChatIdsWithLink(testLinkId)).thenReturn(List.of(chat.getId()));

        List<Long> chatIds = jdbcChatService.findAllChatsIdsWithLink(testLinkId);
        assertThat(chatIds).hasSize(1);
        assertThat(chatIds.getFirst()).isEqualTo(chat.getId());
    }
}
