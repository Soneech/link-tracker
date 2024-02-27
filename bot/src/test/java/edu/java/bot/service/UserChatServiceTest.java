package edu.java.bot.service;

import edu.java.bot.model.UserChat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserChatServiceTest {
    private UserChatService userChatService;

    private UserChat firstUserChat;

    private UserChat secondUserChat;

    private List<String> links;

    private final String testLink = "https://github.com/pengrad";

    @BeforeEach
    public void setUp() {
        userChatService = new UserChatService();

        links = new ArrayList<>(List.of(
            "https://github.com/pengrad/java-telegram-bot-api",
            "https://stackoverflow.com/questions/66696828/how-to-use-configurationproperties-with-records"
        ));
        firstUserChat = new UserChat(333L, new ArrayList<>());
        secondUserChat = new UserChat(777L, links);
    }

    @Test
    public void testUserRegistration() {
        userChatService.register(firstUserChat);
        Optional<UserChat> foundChat = userChatService.findChat(firstUserChat.getChatId());

        assertThat(foundChat).isPresent();
        assertThat(foundChat.get()).isEqualTo(firstUserChat);
    }

    @Test
    public void testGettingUserLinks() {
        userChatService.register(secondUserChat);
        assertThat(userChatService.getUserLinks(secondUserChat.getChatId())).isEqualTo(links);

        userChatService.register(firstUserChat);
        assertThat(userChatService.getUserLinks(firstUserChat.getChatId())).isEmpty();
    }

    @Test
    public void testContainsLink() {
        userChatService.register(secondUserChat);
        assertTrue(userChatService.containsLink(secondUserChat.getChatId(), links.getFirst()));
        assertFalse(userChatService.containsLink(secondUserChat.getChatId(), testLink));
    }

    @Test
    public void testAddingLink() {
        userChatService.register(firstUserChat);

        userChatService.addLink(firstUserChat.getChatId(), testLink);
        assertTrue(userChatService.containsLink(firstUserChat.getChatId(), testLink));
    }

    @Test
    public void testRemovalLink() {
        userChatService.register(secondUserChat);
        String removingLink = links.get(1);

        userChatService.removeLink(secondUserChat.getChatId(), removingLink);
        assertFalse(userChatService.containsLink(secondUserChat.getChatId(), removingLink));
    }
}
