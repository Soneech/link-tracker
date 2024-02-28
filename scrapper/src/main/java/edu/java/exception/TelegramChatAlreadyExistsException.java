package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TelegramChatAlreadyExistsException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public TelegramChatAlreadyExistsException(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.description = "Чат уже зарегистрирован.";
    }
}
