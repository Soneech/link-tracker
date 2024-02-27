package edu.java.bot.command;

import edu.java.bot.model.UserChat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest extends CommandTest {
    @InjectMocks
    private StartCommand startCommand;

    @Test
    @Override
    void testThatReturnedCommandTypeIsCorrect() {
        assertThat(startCommand.type()).isEqualTo(CommandInfo.START.getType());
    }

    @Test
    @Override
    void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(startCommand.description()).isEqualTo(CommandInfo.START.getDescription());
    }

    @Test
    void testThatNewUserAddedToRepository() {
        startCommand.processCommand(update);

        Optional<UserChat> userChat = userChatService.findChat(chatId);
        assertThat(userChat).isPresent();
    }

    @Test
    void testThatCommandReturnCorrectMessageForNewUser() {
        String message = "Приветствую! Вы зарегистрировались в приложении Link Tracker!\n"
            + "Чтобы вывести список доступных команд, используйте /help";
        assertThat(startCommand.processCommand(update).getParameters().get("text")).isEqualTo(message);
    }

    @Test
    void testThatCommandReturnCorrectMessageForRegisteredUser() {
        startCommand.processCommand(update);

        String message = "Вы уже зарегистрированы :)\n"
            + "Чтобы вывести список доступных команд, используйте /help";
        String botMessage = startCommand.processCommand(update).getParameters().get("text").toString();

        assertThat(botMessage).isEqualTo(message);
    }
}
