package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TelegramChatNotFoundException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String description;

    public TelegramChatNotFoundException(String message) {
        super(message);
        this.statusCode = HttpStatus.NOT_FOUND;
        this.description = "Чат не найден.";
    }
}
