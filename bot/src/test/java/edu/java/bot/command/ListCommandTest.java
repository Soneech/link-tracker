package edu.java.bot.command;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.ApiBadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCommandTest extends CommandTest {
    @InjectMocks
    private ListCommand listCommand;

    @Mock
    private ScrapperClient scrapperWebClient;

    private static final String EMPTY_LINKS_LIST_MESSAGE =
        "Список отслеживаемых ссылок пуст. Для добавления ссылки используйте " + CommandInfo.TRACK.getType();

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
    public void testReturnedMessageWithListOfLinks() {
        ListLinksResponse response = new ListLinksResponse(
            new ArrayList<>(
                List.of(
                    new LinkResponse(0L, URI.create("https://github.com/sanyarnd/java-course-2023-backend-template")),
                    new LinkResponse(1L, URI.create("https://github.com/sanyarnd/java-course-2023"))
                )
            ),
            2
        );

        when(scrapperWebClient.getLinks(chatId)).thenReturn(response);

        StringBuilder message = new StringBuilder();
        message.append("Список отслеживаемых ссылок:");
        for (var link: response.links()) {
            message.append("\n").append(link.uri().toString());
        }

        assertThat(listCommand.processCommand(update).getParameters().get("text")).isEqualTo(message.toString());
    }

    @Test
    public void testReturnedMessageWhenListOfLinksIsEmpty() {
        when(scrapperWebClient.getLinks(chatId)).thenReturn(new ListLinksResponse(Collections.emptyList(), 0));
        assertThat(listCommand.processCommand(update).getParameters().get("text")).isEqualTo(EMPTY_LINKS_LIST_MESSAGE);
    }
}
