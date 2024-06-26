package edu.java.bot.command.chain.impl.track;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.exception.AddedResourceNotExistsException;
import edu.java.bot.exception.BadRequestException;
import edu.java.bot.exception.NotFoundException;
import edu.java.bot.exception.ResourceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
public class LinkTrackingCommandStep implements TrackCommandStep {

    private final ScrapperClient scrapperWebClient;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LINK_ALREADY_ADDED_MESSAGE =
        "Данная ссылка уже добавлена. " + CommandInfo.LIST.getType();

    private static final String LINK_SUCCESSFULLY_ADDED_MESSAGE =
        "Ссылка успешно добавлена к отслеживанию. " + CommandInfo.LIST.getType();

    private static final String SOMETHING_WENT_WRONG = "Что-то пошло не так :(";

    private static final String LINK_NOT_EXISTS_MESSAGE = "Кажется, такой ссылки нет :(";

    private static final String RESOURCE_UNAVAILABLE_MESSAGE =
        "Кажется, ресурс временно недоступен. Попробуйте позже.";

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result;
        String link = messageParts[messageParts.length - 1];

        try {
            LinkResponse response = scrapperWebClient.addLink(chatId, new AddLinkRequest(link));
            result = new Result(LINK_SUCCESSFULLY_ADDED_MESSAGE, true);
            LOGGER.info("ChatID: %d; ссылка %s успешно добавлена к отслеживанию".formatted(chatId, link));
            LOGGER.info(response);

        } catch (BadRequestException exception) {
            result = new Result(LINK_ALREADY_ADDED_MESSAGE, false);
            LOGGER.warn("ChatID: %d; ссылка %s уже добавлена".formatted(chatId, link));
            LOGGER.warn(exception.getApiErrorResponse());

        } catch (AddedResourceNotExistsException exception) {
            result = new Result(LINK_NOT_EXISTS_MESSAGE, false);
            LOGGER.warn("ChatID: %d; ссылка %s не существует".formatted(chatId, link));
            LOGGER.warn(exception.getApiErrorResponse());

        } catch (ResourceUnavailableException exception) {
            result = new Result(RESOURCE_UNAVAILABLE_MESSAGE, false);
            LOGGER.warn("ChatID: %d; добавляемый ресурс %s недоступен".formatted(chatId, link));
            LOGGER.warn(exception.getApiErrorResponse());

        } catch (NotFoundException exception) {
            result = new Result(SOMETHING_WENT_WRONG, false);
            LOGGER.warn(exception.getApiErrorResponse());
        }

        return result;
    }
}
