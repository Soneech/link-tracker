package edu.java.bot.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandInfo {
    START("/start", "Зарегистрировать пользователя."),
    HELP("/help", "Вывести окно с командами."),
    TRACK("/track", "Начать отслеживание ссылки (после команды нужно указать ссылку)."),
    UNTRACK("/untrack", "Прекратить отслеживание ссылки (после команды нужно указать ссылку)."),
    LIST("/list", "Показать список отслеживаемых ссылок.");

    private final String type;
    private final String description;
}
