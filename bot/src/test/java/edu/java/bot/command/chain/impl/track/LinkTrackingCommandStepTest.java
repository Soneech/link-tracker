package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LinkTrackingCommandStepTest extends TrackCommandStepTest {
    @InjectMocks
    private LinkTrackingCommandStep linkTrackingCommandStep;

    private static final String LINK_ALREADY_ADDED_MESSAGE =
        "Данная ссылка уже добавлена. " + CommandInfo.LIST.getType();

    private static final String LINK_SUCCESSFULLY_ADDED_MESSAGE =
        "Ссылка успешно добавлена к отслеживанию. " + CommandInfo.LIST.getType();

    private static final String GIT_HUB_LINK = "https://github.com/pengrad/java-telegram-bot-api";

    @Test
    public void testLinkAlreadyAdded() {
        result = new Result(LINK_ALREADY_ADDED_MESSAGE, false);
        messageParts = new String[] {"/track", GIT_HUB_LINK};

        doReturn(true).when(userChatRepository).containsLink(chatId, GIT_HUB_LINK);
        assertThat(linkTrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testThatNewLinkWasAdded() {
        result = new Result(LINK_SUCCESSFULLY_ADDED_MESSAGE, true);
        messageParts = new String[] {"/track", GIT_HUB_LINK};

        doReturn(false).when(userChatRepository).containsLink(chatId, GIT_HUB_LINK);
        assertThat(linkTrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
