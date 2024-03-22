package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LinkValidationCommandStepTest extends TrackCommandStepTest {
    private LinkValidationCommandStep linkValidationCommandStep;

    private static final String INVALID_LINK_MESSAGE = "Кажется, это не ссылка :)";

    private static final String NOT_LINK = "qwertyuiop[519844sfw e";

    private static final String LINK_WITH_SPACES = "https://github.com   /Soneech";

    private static final String STACK_OVERFLOW_LINK =
        "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    @BeforeEach
    public void setUp() {
        linkValidationCommandStep = new LinkValidationCommandStep();
    }

    @Test
    public void testWithValidLink() {
        result = new Result("", true);
        messageParts = new String[] {CommandInfo.TRACK.getType(), STACK_OVERFLOW_LINK};
        assertThat(linkValidationCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testWithInvalidLink() {
        result = new Result(INVALID_LINK_MESSAGE, false);
        messageParts = new String[] {CommandInfo.TRACK.getType(), NOT_LINK};
        assertThat(linkValidationCommandStep.handle(messageParts, chatId)).isEqualTo(result);

        messageParts[1] = LINK_WITH_SPACES;
        assertThat(linkValidationCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
