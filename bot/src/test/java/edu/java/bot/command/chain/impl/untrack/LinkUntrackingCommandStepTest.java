package edu.java.bot.command.chain.impl.untrack;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.ApiNotFoundException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkUntrackingCommandStepTest {
    @InjectMocks
    private LinkUntrackingCommandStep linkUntrackingCommandStep;

    @Mock
    private ScrapperClient scrapperWebClient;

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

        when(scrapperWebClient.deleteLink(chatId, new RemoveLinkRequest(GIT_HUB_LINK)))
            .thenReturn(new LinkResponse(0L, URI.create(GIT_HUB_LINK)));
        assertThat(linkUntrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }

    @Test
    public void testLinkNotFound() {
        String[] messageParts = new String[] {CommandInfo.UNTRACK.getType(), STACK_OVERFLOW_LINK};
        Result result = new Result(NOT_FOUND_MESSAGE, false);

        when(scrapperWebClient.deleteLink(chatId, new RemoveLinkRequest(STACK_OVERFLOW_LINK)))
            .thenThrow(ApiNotFoundException.class);
        assertThat(linkUntrackingCommandStep.handle(messageParts, chatId)).isEqualTo(result);
    }
}
