package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.repository.UserChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
public class LinkTrackingCommandStep implements TrackCommandStep {

    private final UserChatRepository userChatRepository;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LINK_ALREADY_ADDED_MESSAGE =
        "Данная ссылка уже добавлена. " + CommandInfo.LIST.getType();

    private static final String LINK_SUCCESSFULLY_ADDED_MESSAGE =
        "Ссылка успешно добавлена к отслеживанию. " + CommandInfo.LIST.getType();

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        Result result;

        String link = messageParts[messageParts.length - 1];

        if (userChatRepository.containsLink(chatId, link)) {
            result = new Result(LINK_ALREADY_ADDED_MESSAGE, false);
            LOGGER.warn("ChatID: %d; link %s has already added".formatted(chatId, link));
        } else {
            result = new Result(LINK_SUCCESSFULLY_ADDED_MESSAGE, true);
            userChatRepository.addLink(chatId, link);
            LOGGER.info("ChatID: %d; link %s successfully added".formatted(chatId, link));
        }
        return result;
    }
}
