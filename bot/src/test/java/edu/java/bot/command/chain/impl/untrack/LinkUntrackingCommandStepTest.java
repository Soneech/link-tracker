package edu.java.bot.command.chain.impl.untrack;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.repository.UserChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LinkUntrackingCommandStepTest {
    @InjectMocks
    private LinkUntrackingCommandStep linkUntrackingCommandStep;

    @Spy
    private UserChatRepository userChatRepository;

    private Long chatId;

    private static final String GIT_HUB_LINK = "https://github.com/pengrad/java-telegram-bot-api";

    private static final String STACK_OVERFLOW_LINK =
        "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    private static final String SUCCESSFUL_LINK_REMOVAL_MESSAGE =
        "Сылка успешно удалена. " + CommandInfo.LIST.getType();

    private static final String NOT_FOUND_MESSAGE =
        "Ничего не найдено :( " + CommandInfo.LIST.getType();

    @BeforeEach
    public void setUp() {
        chatId = 333L;
    }

    @Test
    public void testSuccessfulLinkRemoval() {
        String[] messageParts = new String[] {CommandInfo.UNTRACK.getType(), GIT_HUB_LINK};
        Result result = new Result(SUCCESSFUL_LINK_REMOVAL_MESSAGE, true);

        doReturn(true).when(userChatRepository).containsLink(chatId, GIT_HUB_LINK);
        assertThat(linkUntrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testLinkNotFound() {
        String[] messageParts = new String[] {CommandInfo.UNTRACK.getType(), STACK_OVERFLOW_LINK};
        Result result = new Result(NOT_FOUND_MESSAGE, false);

        assertThat(linkUntrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
