package edu.java.bot.repository;

import edu.java.bot.model.UserChat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserChatRepositoryTest {
    private UserChatRepository userChatRepository;

    private UserChat firstUserChat;

    private UserChat secondUserChat;

    private List<String> links;

    private final String testLink = "https://github.com/pengrad";

    @BeforeEach
    public void setUp() {
        userChatRepository = new UserChatRepository();

        links = new ArrayList<>(List.of(
            "https://github.com/pengrad/java-telegram-bot-api",
            "https://stackoverflow.com/questions/66696828/how-to-use-configurationproperties-with-records"
        ));
        firstUserChat = new UserChat(333L, new ArrayList<>());
        secondUserChat = new UserChat(777L, links);
    }

    @Test
    public void testUserRegistration() {
        userChatRepository.register(firstUserChat);
        assertThat(userChatRepository.findChat(firstUserChat.getChatId())).isEqualTo(firstUserChat);
    }

    @Test
    public void testGettingUserLinks() {
        userChatRepository.register(secondUserChat);
        assertThat(userChatRepository.getUserLinks(secondUserChat.getChatId())).isEqualTo(links);

        userChatRepository.register(firstUserChat);
        assertThat(userChatRepository.getUserLinks(firstUserChat.getChatId())).isEmpty();
    }

    @Test
    public void testContainsLink() {
        userChatRepository.register(secondUserChat);
        assertTrue(userChatRepository.containsLink(secondUserChat.getChatId(), links.get(0)));
        assertFalse(userChatRepository.containsLink(secondUserChat.getChatId(), testLink));
    }

    @Test
    public void testAddingLink() {
        userChatRepository.register(firstUserChat);

        userChatRepository.addLink(firstUserChat.getChatId(), testLink);
        assertTrue(userChatRepository.containsLink(firstUserChat.getChatId(), testLink));
    }

    @Test
    public void testRemovalLink() {
        userChatRepository.register(secondUserChat);
        String removingLink = links.get(1);

        userChatRepository.removeLink(secondUserChat.getChatId(), removingLink);
        assertFalse(userChatRepository.containsLink(secondUserChat.getChatId(), removingLink));
    }
}
