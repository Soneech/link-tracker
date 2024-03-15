package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.parser.LinkParser;
import edu.java.bot.website.WebsiteInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
public class LinkParsingCommandStep implements TrackCommandStep {
    private final List<LinkParser> parsers;

    private static final String UNSUPPORTED_SERVICE_MESSAGE =
        "Я не могу отлеживать данную ссылку :(\nЯ могу отслеживать следующие ресурсы:\n%s"
            + "Для более подробной информации используйте " + CommandInfo.HELP.getType();

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result = new Result("", true);
        String link = messageParts[messageParts.length - 1];

        boolean parsedAtLeastOne = false;
        for (var parser: parsers) {
            if (parser.isLinkCorrect(link)) {
                parsedAtLeastOne = true;
                break;
            }
        }

        if (!parsedAtLeastOne) {
            StringBuilder supportedServices = new StringBuilder();
            for (var service: WebsiteInfo.values()) {
                supportedServices.append(service.getDomain()).append(" - ")
                    .append(service.getDescription()).append("\n");
            }

            result.setMessage(UNSUPPORTED_SERVICE_MESSAGE.formatted(supportedServices.toString()));
            result.setSuccess(false);
            LOGGER.warn("ChatID: %d; ссылка на неподдерживаемый сервис: %s".formatted(chatId, link));
        }

        return result;
    }


}
