package edu.java.bot.command;

import edu.java.bot.command.track.TrackCommand;
import edu.java.bot.service.LinkParsingProcessor;
import edu.java.bot.util.LinkValidator;
import edu.java.bot.website.WebsiteInfo;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TrackCommandTest extends CommandTest {
    @InjectMocks
    private TrackCommand trackCommand;

    @Mock
    private LinkValidator linkValidator;

    @Mock
    private LinkParsingProcessor linkParsingProcessor;

    private static final String GIT_HUB_LINK = "https://github.com/pengrad/java-telegram-bot-api";

    private static final String STACK_OVERFLOW_LINK =
        "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    private static final String NOT_LINK = "qwertyuiop[519844sfw e";

    private static final String LINK_WITH_SPACES = "https://github.com   /Soneech";

    private static final String UNSUPPORTED_SERVICE_LINK = "https://www.youtube.com/@strannoemestechko";

    @Test
    @Override
    void testThatReturnedCommandTypeIsCorrect() {
        assertThat(trackCommand.type()).isEqualTo(CommandInfo.TRACK.getType());
    }

    @Test
    @Override
    void testThatReturnedCommandDescriptionIsCorrect() {
        assertThat(trackCommand.description()).isEqualTo(CommandInfo.TRACK.getDescription());
    }

    @Test
    public void testIncorrectCommandFormatMessage() {
        doReturn("/track").when(message).text();
        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo("Неверный формат команды. /help");
    }

    @Test
    public void testLinkAlreadyAddedMessage() {
        doReturn(true).when(userChatRepository).containsLink(chatId, GIT_HUB_LINK);
        doReturn("/track " + GIT_HUB_LINK).when(message).text();

        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo("Данная ссылка уже добавлена. /list");
    }

    @Test
    public void testIncorrectLinkMessage() {
        String botMessage = "Некорректная ссылка.";

        doReturn(null).when(linkValidator).validateLinkAndGetURI(LINK_WITH_SPACES);
        doReturn("/track " + LINK_WITH_SPACES).when(message).text();
        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo(botMessage);

        doReturn(null).when(linkValidator).validateLinkAndGetURI(NOT_LINK);
        doReturn("/track " + NOT_LINK).when(message).text();
        assertThat(trackCommand.processCommand(update).getParameters().get("text")).isEqualTo(botMessage);
    }

    @Test
    public void testThatCorrectNewGitHubLinkWasAdded() {
        URI uri = URI.create(GIT_HUB_LINK);

        doReturn(uri).when(linkValidator).validateLinkAndGetURI(GIT_HUB_LINK);
        doReturn(true).when(linkParsingProcessor).processParsing(uri);
        doReturn("/track " + GIT_HUB_LINK).when(message).text();

        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo("Ссылка успешно добавлена к отслеживанию. /list");
    }

    @Test
    public void testThatCorrectNewStackOverflowLinkWasAdded() {
        URI uri = URI.create(STACK_OVERFLOW_LINK);
        doReturn(uri).when(linkValidator).validateLinkAndGetURI(STACK_OVERFLOW_LINK);
        doReturn(true).when(linkParsingProcessor).processParsing(uri);
        doReturn("/track " + STACK_OVERFLOW_LINK).when(message).text();

        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo("Ссылка успешно добавлена к отслеживанию. /list");
    }

    @Test
    public void testUnsupportedWebservicesMessage() {
        StringBuilder botMessage = new StringBuilder();
        botMessage.append("Данный сервис не поддерживается :(\nПоддерживаемые сервисы приведены ниже:");
        for (var website: WebsiteInfo.values()) {
            botMessage.append("\n").append(website.getDomain());
        }

        URI firstURI = URI.create(UNSUPPORTED_SERVICE_LINK);
        doReturn(firstURI).when(linkValidator).validateLinkAndGetURI(UNSUPPORTED_SERVICE_LINK);
        doReturn(false).when(linkParsingProcessor).processParsing(firstURI);
        doReturn("/track " + UNSUPPORTED_SERVICE_LINK).when(message).text();

        assertThat(trackCommand.processCommand(update).getParameters().get("text"))
            .isEqualTo(botMessage.toString());
    }
}
