package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.dto.response.ResponseMessage;
import edu.java.bot.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final CommandInfo commandInfo = CommandInfo.START;

    private final ScrapperClient scrapperWebClient;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String WELCOME_MESSAGE =
        "Приветствую, %s! Вы зарегистрировались в приложении Link Tracker!";

    private static final String ALREADY_REGISTERED_MESSAGE = "Вы уже зарегистрированы :)";

    private static final String SUPPORTED_COMMANDS_MESSAGE =
        "Чтобы вывести список доступных команд, используйте " + CommandInfo.HELP.getType();

    @Override
    public SendMessage processCommand(Update update) {
        StringBuilder botMessage = new StringBuilder();
        Long chatId = update.message().chat().id();
        String username = update.message().chat().username();

        try {
            ResponseMessage response = scrapperWebClient.registerChat(chatId);
            botMessage.append(WELCOME_MESSAGE.formatted(username));
            botMessage.append("\n").append(SUPPORTED_COMMANDS_MESSAGE);
            LOGGER.info(response.message());

        } catch (BadRequestException exception) {
            botMessage.append(ALREADY_REGISTERED_MESSAGE);
            LOGGER.warn(exception.getApiErrorResponse());
        }

        return new SendMessage(chatId, botMessage.toString());
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
