package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.chain.CommandStep;
import edu.java.bot.command.chain.Result;
import java.util.List;

public interface Command {
    String SOMETHING_WENT_WRONG = "Что-то пошло не так :(";

    SendMessage processCommand(Update update);

    String type();

    String description();

    default BotCommand toApiCommand() {
        return new BotCommand(type(), description());
    }

    default Result processChain(List<? extends CommandStep> chain, String[] messageParts, Long chatId) {
        Result result = new Result(SOMETHING_WENT_WRONG, false);
        for (var step: chain) {
            result = step.handle(messageParts, chatId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }
}
