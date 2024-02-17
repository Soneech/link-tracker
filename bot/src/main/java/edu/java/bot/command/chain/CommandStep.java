package edu.java.bot.command.chain;


public interface CommandStep {
    Result handle(String[] messageParts, Long chatId);
}
