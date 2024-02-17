package edu.java.bot.command.chain.impl.defaults;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CommandFormatStep implements DefaultCommandStep {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String INCORRECT_COMMAND_FORMAT_MESSAGE =
        "Неверный формат команды. " + CommandInfo.HELP.getType();

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result = new Result("", true);

        if (messageParts.length != 2) {
            result.setMessage(INCORRECT_COMMAND_FORMAT_MESSAGE);
            result.setSuccess(false);
            LOGGER.warn("ChatID: %d; incorrect command format: %s"
                .formatted(chatId, String.join(" ", messageParts)));
        }

        return result;
    }
}
