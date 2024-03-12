package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.chain.Result;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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

    private static final String INVALID_LINK_MESSAGE = "Кажется, такой ссылки не существует :(";

    private static final String SOMETHING_WENT_WRONG = "Что-то пошло не так :(";

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result = new Result("", true);
        String link = messageParts[messageParts.length - 1];

        if (!linkPattern.matcher(link).matches()) {
            makeFailedResult(result, INVALID_LINK_MESSAGE);
            LOGGER.warn("ChatID: %d; invalid link: %s".formatted(chatId, link));
        } else {
            try {
                URL url = URI.create(link).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    makeFailedResult(result, INVALID_LINK_MESSAGE);
                }
            } catch (IOException e) {
                LOGGER.error(e.getStackTrace());
                makeFailedResult(result, SOMETHING_WENT_WRONG);
            }
        }

        return result;
    }

    public void makeFailedResult(Result result, String message) {
        result.setSuccess(false);
        result.setMessage(message);
    }
}
