package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TelegramChatNotFoundException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String description;

    public TelegramChatNotFoundException(Long id) {
        super("Чат с id %d не найден.".formatted(id));
        this.statusCode = HttpStatus.NOT_FOUND;
        this.description = "Чат не найден.";
    }
}
