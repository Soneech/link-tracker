package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.parser.GitHubParser;
import edu.java.bot.parser.LinkParser;
import edu.java.bot.parser.StackOverflowParser;
import edu.java.bot.website.WebsiteInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class LinkParsingCommandStepTest extends TrackCommandStepTest {
    private LinkParsingCommandStep linkParsingCommandStep;

    @Mock
    private GitHubParser gitHubParser;

    @Mock
    private StackOverflowParser stackOverflowParser;

    private static final String GIT_HUB_LINK = "https://github.com/pengrad/java-telegram-bot-api";

    private static final String STACK_OVERFLOW_LINK =
        "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    private static final String UNSUPPORTED_SERVICE_LINK = "https://www.youtube.com/@strannoemestechko";

    private static final String UNSUPPORTED_SERVICE_MESSAGE =
        "Я не могу отлеживать данную ссылку :(\nЯ могу отслеживать следующие ресурсы:\n%s"
            + "Для более подробной информации используйте " + CommandInfo.HELP.getType();

    @BeforeEach
    public void setUp() {
        List<LinkParser> parsers = List.of(gitHubParser, stackOverflowParser);
        linkParsingCommandStep = new LinkParsingCommandStep(parsers);
    }

    @Test
    public void testParsingWithGitHubAndStackOverflowParsers() {
        lenient().doReturn(true).when(gitHubParser).isLinkCorrect(GIT_HUB_LINK);
        lenient().doReturn(true).when(stackOverflowParser).isLinkCorrect(STACK_OVERFLOW_LINK);

        result = new Result("", true);
        messageParts = new String[] {CommandInfo.TRACK.getType(), GIT_HUB_LINK};
        assertThat(linkParsingCommandStep.handle(messageParts, chatId)).isEqualTo(result);

        messageParts[1] = STACK_OVERFLOW_LINK;
        assertThat(linkParsingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testWithUnsupportedServiceLink() {
        StringBuilder supportedServices = new StringBuilder();
        for (var service: WebsiteInfo.values()) {
            supportedServices.append(service.getDomain()).append(" - ")
                .append(service.getDescription()).append("\n");
        }
        result = new Result(UNSUPPORTED_SERVICE_MESSAGE.formatted(supportedServices.toString()), false);
        messageParts = new String[] {CommandInfo.TRACK.getType(), UNSUPPORTED_SERVICE_LINK};

        assertThat(linkParsingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
