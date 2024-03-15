package edu.java.bot.command;

import edu.java.bot.website.WebsiteInfo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest extends CommandTest {
    private HelpCommand helpCommand;

    private Command testCommand;

    @Override
    public void setUp() {
        super.setUp();
        testCommand = mock(ListCommand.class);
        lenient().when(testCommand.type()).thenReturn(CommandInfo.LIST.getType());
        lenient().when(testCommand.description()).thenReturn(CommandInfo.LIST.getDescription());

        helpCommand = new HelpCommand(List.of(testCommand));
    }

    @Test
    public void testThatReturnedCommandTypeIsCorrect() {
        assertThat(helpCommand.type()).isEqualTo(CommandInfo.HELP.getType());
    }

    @Test
    public void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(helpCommand.description()).isEqualTo(CommandInfo.HELP.getDescription());
    }

    @Test
    public void testThatMessageWithCommandListIsCorrect() {
        StringBuilder botMessage = new StringBuilder();
        botMessage
            .append("Список поддерживаемых команд:").append("\n")
            .append("/list - Показать список отслеживаемых ссылок.").append("\n\n")
            .append("Ниже приведены поддерживаемые сервисы:");
        for (var website: WebsiteInfo.values()) {
            botMessage.append("\n").append(website.getDomain()).append(" - ").append(website.getDescription());
        }
        assertThat(helpCommand.processCommand(update).getParameters().get("text")).isEqualTo(botMessage.toString());
    }
}
