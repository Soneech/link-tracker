package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.chain.Result;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class LinkValidationCommandStep implements TrackCommandStep {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Pattern linkPattern =
        Pattern.compile("^(https?|http)(://)([-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");

    private static final String INVALID_LINK_MESSAGE = "Кажется, это не ссылка :)";

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result = new Result("", true);
        String link = messageParts[messageParts.length - 1];

        if (!linkPattern.matcher(link).matches()) {
            makeFailedResult(result, INVALID_LINK_MESSAGE);
            LOGGER.warn("ChatID: %d; invalid link: %s".formatted(chatId, link));
        }

        return result;
    }

    public void makeFailedResult(Result result, String message) {
        result.setSuccess(false);
        result.setMessage(message);
    }
}
