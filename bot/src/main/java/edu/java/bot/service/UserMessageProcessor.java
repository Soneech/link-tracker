package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageProcessor {
    private final Map<String, Command> commands;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Такая команда не поддерживается :(\nЧтобы вывести список доступных команд, используйте "
            + CommandInfo.HELP.getType();

    @Autowired
    public UserMessageProcessor(List<Command> commands) {
        this.commands = new HashMap<>();
        for (var command : commands) {
            this.commands.put(command.type(), command);
        }
    }

    public SendMessage processUpdate(Update update) {
        String botMessage;
        String userMessage = update.message().text();
        String commandType = userMessage.split(" +")[0];

        Long chatId = update.message().chat().id();
        LOGGER.info("ChatID: %d with message: %s".formatted(chatId, userMessage));


        Command command = commands.get(commandType);
        if (command != null) {
            LOGGER.info("ChatID: %d; processing command: %s".formatted(chatId, command.type()));
            return command.processCommand(update);
        }

        LOGGER.error("ChatID: %d; unsupported command: %s".formatted(chatId, userMessage));
        botMessage = UNSUPPORTED_COMMAND_MESSAGE;

        return new SendMessage(chatId, botMessage);
    }
}
