package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandInfo;
import edu.java.bot.exception.ScrapperUnavailableException;
import edu.java.bot.exception.TooManyRequestsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Service
public class UserMessageProcessor {
    private final Map<String, Command> commands;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Такая команда не поддерживается :(\nЧтобы вывести список доступных команд, используйте "
            + CommandInfo.HELP.getType();

    private static final String TOO_MANY_REQUESTS_MESSAGE = "Кажется, вы слишком много хотите\nПопробуйте позже :)";

    private static final String SERVICE_UNAVAILABLE_MESSAGE = "Функция временно недоступна. Попробуйте позже";

    @Autowired
    public UserMessageProcessor(List<Command> commands) {
        this.commands = new HashMap<>();
        for (var command : commands) {
            this.commands.put(command.type(), command);
        }
    }

    public SendMessage processUpdate(Update update) {
        String userMessage = update.message().text();
        String commandType = userMessage.split(" +")[0];

        Long chatId = update.message().chat().id();
        LOGGER.info("ChatID: %d with message: %s".formatted(chatId, userMessage));

        Command command = commands.get(commandType);
        if (command != null) {
            SendMessage sendMessage;
            LOGGER.info("ChatID: %d; processing command: %s".formatted(chatId, command.type()));
            try {
                sendMessage = command.processCommand(update);

            } catch (TooManyRequestsException exception) {
                LOGGER.warn("ChatID: %d; Too Many Requests!".formatted(chatId));
                LOGGER.warn(exception.getApiErrorResponse());
                sendMessage = new SendMessage(chatId, TOO_MANY_REQUESTS_MESSAGE);

            } catch (WebClientRequestException exception) {
                LOGGER.error("Scrapper недоступен; %s".formatted(exception.getMessage()));
                sendMessage = new SendMessage(chatId, SERVICE_UNAVAILABLE_MESSAGE);

            } catch (ScrapperUnavailableException exception) {
                LOGGER.error("Scrapper недоступен: %s; status code: %s"
                    .formatted(exception.getMessage(), exception.getHttpStatusCode()));
                sendMessage = new SendMessage(chatId, SERVICE_UNAVAILABLE_MESSAGE);
            }
            return sendMessage;
        }

        LOGGER.error("ChatID: %d; unsupported command: %s".formatted(chatId, userMessage));
        return new SendMessage(chatId, UNSUPPORTED_COMMAND_MESSAGE);
    }
}
