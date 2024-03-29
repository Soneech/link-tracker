package edu.java.bot.command.chain.impl.untrack;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import edu.java.bot.exception.ScrapperUnavailableException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class LinkUntrackingCommandStep implements UntrackCommandStep {

    private final ScrapperClient scrapperWebClient;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String SUCCESSFUL_LINK_REMOVAL_MESSAGE =
        "Сылка успешно удалена. " + CommandInfo.LIST.getType();

    private static final String NOT_FOUND_MESSAGE =
        "Ничего не найдено :( " + CommandInfo.LIST.getType();

    private static final String SOMETHING_WENT_WRONG = "Что-то пошло не так :(";

    private static final String SERVICE_UNAVAILABLE_MESSAGE = "Функция временно недоступна. Попробуйте позже";

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        String link = messageParts[messageParts.length - 1];
        Result result;

        try {
            LinkResponse response = scrapperWebClient.deleteLink(chatId, new RemoveLinkRequest(link));
            result = new Result(SUCCESSFUL_LINK_REMOVAL_MESSAGE, true);
            LOGGER.info("ChatID: %d; ссылка %s успешно удалена".formatted(chatId, link));
            LOGGER.info(response);

        } catch (ApiBadRequestException exception) {
            result = new Result(SOMETHING_WENT_WRONG, false);
            LOGGER.warn(exception.getApiErrorResponse());

        } catch (ApiNotFoundException exception) {
            result = new Result(NOT_FOUND_MESSAGE, false);
            LOGGER.warn("ChatID: %d; ссылка %s не найдена в списке отслеживаемых".formatted(chatId, link));
            LOGGER.warn(exception.getApiErrorResponse());

        } catch (ScrapperUnavailableException exception) {
            result = new Result(SERVICE_UNAVAILABLE_MESSAGE, false);
            LOGGER.error("Scrapper недоступен; %s; status code: %s"
                .formatted(exception.getMessage(), exception.getHttpStatusCode()));
        }

        return result;
    }
}
