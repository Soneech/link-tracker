package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.chain.Result;
import edu.java.bot.command.chain.impl.untrack.UntrackCommandStep;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final CommandInfo commandInfo = CommandInfo.UNTRACK;

    private final List<UntrackCommandStep> chain;

    @Override
    public SendMessage processCommand(Update update) {
        Long chatId = update.message().chat().id();
        String[] messageParts = update.message().text().split(" +", 2);

        Result result = processChain(chain, messageParts, chatId);
        return new SendMessage(chatId, result.getMessage());
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
