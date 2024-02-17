package edu.java.bot.command.chain.impl.untrack;

import edu.java.bot.command.CommandInfo;
import edu.java.bot.command.chain.Result;
import edu.java.bot.repository.UserChatRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class LinkUntrackingCommandStep implements UntrackCommandStep {

    private final UserChatRepository userChatRepository;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String SUCCESSFUL_LINK_REMOVAL_MESSAGE =
        "Сылка успешно удалена. " + CommandInfo.LIST.getType();

    private static final String NOT_FOUND_MESSAGE =
        "Ничего не найдено :( " + CommandInfo.LIST.getType();

    @Override
    public Result handle(String[] messageParts, Long chatId) {
        String link = messageParts[messageParts.length - 1];

        Result result;
        if (userChatRepository.containsLink(chatId, link)) {
            userChatRepository.removeLink(chatId, link);
            result = new Result(SUCCESSFUL_LINK_REMOVAL_MESSAGE, true);
            LOGGER.info("ChatID: %d; link %s successfully removed".formatted(chatId, link));
        } else {
            result = new Result(NOT_FOUND_MESSAGE, false);
            LOGGER.warn("ChatID: %d; link %s not found in tracked list".formatted(chatId, link));
        }

        return result;
    }
}
