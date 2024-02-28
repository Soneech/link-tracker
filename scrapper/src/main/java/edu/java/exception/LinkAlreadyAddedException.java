package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LinkAlreadyAddedException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public LinkAlreadyAddedException(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.description = "Ссылка уже добавлена.";
    }
}
