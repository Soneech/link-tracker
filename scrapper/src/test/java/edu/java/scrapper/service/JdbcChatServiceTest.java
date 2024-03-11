package edu.java.scrapper.service;

import edu.java.dao.jdbc.JdbcChatDao;
import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.service.ChatService;
import edu.java.service.jdbc.JdbcChatService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JdbcChatServiceTest extends JdbcServiceTest {
    private static ChatService chatService;

    @BeforeEach
    public void chatServiceSetUp() {
        jdbcChatDao = mock(JdbcChatDao.class);
        chatService = new JdbcChatService(jdbcChatDao);
    }

    @Test
    public void testRegistration() {
        chatService.registerChat(chat);
        verify(jdbcChatDao).save(chat);
    }

    @Test
    public void testRepeatedRegistration() {
        when(jdbcChatDao.findById(chat.getId())).thenReturn(Optional.of(chat));

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> chatService.registerChat(chat));
    }

    @Test
    public void testUnregisterUser() {
        when(jdbcChatDao.findById(chat.getId())).thenReturn(Optional.of(chat));
        chatService.unregisterChat(chat.getId());

        verify(jdbcChatDao).delete(chat.getId());
    }

    @Test
    public void testUnregisterNonExistentChat() {
        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> chatService.unregisterChat(chat.getId()));
    }

    @Test
    public void testFindChat() {
        chatService.findChat(chat.getId());
        verify(jdbcChatDao).findById(chat.getId());
    }
}
