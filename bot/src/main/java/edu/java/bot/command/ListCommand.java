package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import edu.java.bot.exception.ScrapperUnavailableException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final CommandInfo commandInfo = CommandInfo.LIST;

    private final ScrapperClient scrapperWebClient;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String EMPTY_LINKS_LIST_MESSAGE =
        "Список отслеживаемых ссылок пуст. Для добавления ссылки используйте " + CommandInfo.TRACK.getType();

    private static final String LINKS_LIST_MESSAGE_TITLE = "Список отслеживаемых ссылок:";

    private static final String SOMETHING_WENT_WRONG = "Что-то пошло не так :(";

    private static final String SERVICE_UNAVAILABLE_MESSAGE = "Функция временно недоступна. Попробуйте позже";

    @Override
    public SendMessage processCommand(Update update) {
        StringBuilder botMessage = new StringBuilder();
        Long chatId = update.message().chat().id();

        try {
            ListLinksResponse response = scrapperWebClient.getLinks(chatId);
            if (response.size() == 0) {
                botMessage.append(EMPTY_LINKS_LIST_MESSAGE);
                LOGGER.info("ChatID: %d; command: %s; result: список ссылок пуст".formatted(chatId, type()));
            } else {
                botMessage.append(LINKS_LIST_MESSAGE_TITLE);
                response.links().forEach(link ->
                    botMessage.append("\n").append(link.uri().toString()).append("\n"));

                LOGGER.info("ChatID: %d; command: %s; result: список ссылок отправлен".formatted(chatId, type()));
            }

        } catch (ApiBadRequestException | ApiNotFoundException exception) {
            botMessage.append(SOMETHING_WENT_WRONG);
            LOGGER.error(exception.getApiErrorResponse());

        } catch (ScrapperUnavailableException exception) {
            botMessage.append(SERVICE_UNAVAILABLE_MESSAGE);
            LOGGER.error("Scrapper недоступен; %s; status code: %s"
                .formatted(exception.getMessage(), exception.getHttpStatusCode()));
        }

        return new SendMessage(update.message().chat().id(), botMessage.toString());
    }

    @Override
    public String type() {
        return commandInfo.getType();
    }

    @Override
    public String description() {
        return commandInfo.getDescription();
    }
}
