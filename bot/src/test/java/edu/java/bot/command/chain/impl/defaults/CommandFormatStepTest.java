package edu.java.bot.command.chain.impl.defaults;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommandFormatStepTest {
    private CommandFormatStep commandFormatStep;

    private static final String INCORRECT_COMMAND_FORMAT_MESSAGE =
        "Неверный формат команды. " + CommandInfo.HELP.getType();

    private Long chatId;

    @BeforeEach
    public void setUp() {
        chatId = 777L;
        commandFormatStep = new CommandFormatStep();
    }

    @Test
    public void testCorrectCommandFormat() {
        String[] messageParts = new String[] {CommandInfo.TRACK.getType(), "https://github.com/Soneech"};
        Result result = new Result("", true);

        assertThat(commandFormatStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testIncorrectCommandFormat() {
        String[] messageParts = new String[] {CommandInfo.TRACK.getType()};
        Result result = new Result(INCORRECT_COMMAND_FORMAT_MESSAGE, false);

        assertThat(commandFormatStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
