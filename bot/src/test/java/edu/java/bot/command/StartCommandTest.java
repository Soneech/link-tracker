package edu.java.bot.command;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.response.SuccessMessageResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.model.UserChat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest extends CommandTest {
    @InjectMocks
    private StartCommand startCommand;

    @Mock
    private ScrapperClient scrapperWebClient;

    private static final String WELCOME_MESSAGE =
        "Приветствую! Вы зарегистрировались в приложении Link Tracker!";

    private static final String ALREADY_REGISTERED_MESSAGE = "Вы уже зарегистрированы :)";

    private static final String SUPPORTED_COMMANDS_MESSAGE =
        "Чтобы вывести список доступных команд, используйте " + CommandInfo.HELP.getType();

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
    void testThatCommandReturnCorrectMessageForNewUser() {
        when(scrapperWebClient.registerChat(chatId)).thenReturn(new SuccessMessageResponse("some message"));
        assertThat(startCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo(WELCOME_MESSAGE + "\n" + SUPPORTED_COMMANDS_MESSAGE);
    }

    @Test
    void testThatCommandReturnCorrectMessageForRegisteredUser() {
        when(scrapperWebClient.registerChat(chatId)).thenReturn(new SuccessMessageResponse("some message"));
        startCommand.processCommand(update);

        when(scrapperWebClient.registerChat(chatId)).thenThrow(ApiBadRequestException.class);
        String botMessage = startCommand.processCommand(update).getParameters().get("text").toString();

        assertThat(botMessage).isEqualTo(ALREADY_REGISTERED_MESSAGE + "\n" + SUPPORTED_COMMANDS_MESSAGE);
    }
}
