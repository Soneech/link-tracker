package edu.java.bot.command;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest extends CommandTest {
    @InjectMocks
    private ListCommand listCommand;

    @Test
    @Override
    public void testThatReturnedCommandTypeIsCorrect() {
        assertThat(listCommand.type()).isEqualTo(CommandInfo.LIST.getType());
    }

    @Test
    @Override
    public void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(listCommand.description()).isEqualTo(CommandInfo.LIST.getDescription());
    }

    @Test
    public void testThatReturnedListOfLinksIsCorrect() {
        doReturn(List.of(
            "https://github.com/sanyarnd/java-course-2023-backend-template",
            "https://github.com/sanyarnd/java-course-2023",
            "https://stackoverflow.com/questions/66696828/how-to-use-configurationproperties-with-records"
        )).when(userChatRepository).getUserLinks(chatId);

        StringBuilder message = new StringBuilder();
        message.append("Список отслеживаемых ссылок:");
        for (String link : userChatRepository.getUserLinks(chatId)) {
            message.append("\n").append(link);
        }

        assertThat(listCommand.processCommand(update).getParameters().get("text")).isEqualTo(message.toString());
    }

    @Test
    public void testReturnedMessageWhenListOfLinksIsEmpty() {
        doReturn(Collections.emptyList()).when(userChatRepository).getUserLinks(chatId);

        String message = "Список отслеживаемых ссылок пуст. Для добавления ссылки используйте /track";
        assertThat(listCommand.processCommand(update).getParameters().get("text")).isEqualTo(message);
    }
}
