package edu.java.scrapper.service;

import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.model.Link;
import edu.java.model.UserChat;
import edu.java.service.UserChatService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UserChatServiceTest {
    private UserChatService userChatService;

    private Long firstChatId;

    private Long secondChatId;

    private List<Link> links;

    @BeforeEach
    public void setUp() {
        userChatService = new UserChatService();

        links = List.of(
            new Link("https://github.com/pengrad/java-telegram-bot-api"),
            new Link("https://github.com/pengrad")
        );

        firstChatId = 333L;
        secondChatId = 777L;
    }

    @Test
    public void testRegisterChat() {
        userChatService.registerChat(firstChatId);
        UserChat foundChat = userChatService.findChat(firstChatId);

        assertThat(foundChat.getChatId()).isEqualTo(firstChatId);
    }

    @Test
    public void testFailedRegisterChat() {
        userChatService.registerChat(firstChatId);

        assertThatExceptionOfType(TelegramChatAlreadyExistsException.class)
            .isThrownBy(() -> userChatService.registerChat(firstChatId));
    }

    @Test
    public void testRemoveChat() {
        userChatService.registerChat(secondChatId);
        assertThat(userChatService.findChat(secondChatId)).isNotNull();

        userChatService.removeChat(secondChatId);
        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> userChatService.findChat(secondChatId));
    }

    @Test
    public void testFailedRemoveChat() {
        assertThatExceptionOfType(TelegramChatNotFoundException.class)
            .isThrownBy(() -> userChatService.findChat(secondChatId));
    }

    @Test
    public void testFindChatOrNullable() {
        userChatService.registerChat(firstChatId);
        assertThat(userChatService.findChatOrNullable(firstChatId)).isPresent();

        userChatService.removeChat(firstChatId);
        assertThat(userChatService.findChatOrNullable(firstChatId)).isEmpty();
    }

    @Test
    public void testAddingAndGettingUserLinks() {
        userChatService.registerChat(firstChatId);
        userChatService.addLink(firstChatId, links.getFirst());
        assertThat(userChatService.getUserLinks(firstChatId)).contains(links.getFirst());

        userChatService.addLink(firstChatId, links.getLast());
        assertThat(userChatService.getUserLinks(firstChatId)).isEqualTo(links);
    }
}
