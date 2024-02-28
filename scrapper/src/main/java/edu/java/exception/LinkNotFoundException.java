package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LinkNotFoundException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public LinkNotFoundException(String message) {
        super(message);
        this.statusCode = HttpStatus.NOT_FOUND;
        this.description = "Сылка не найдена.";
    }
}
