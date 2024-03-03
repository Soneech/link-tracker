package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LinkAlreadyAddedException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public LinkAlreadyAddedException(Long id, String link) {
        super("Ссылка %s для чата c id %d уже добавлена.".formatted(link, id));
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.description = "Ссылка уже добавлена.";
    }
}
