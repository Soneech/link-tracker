package edu.java.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LinkNotFoundException extends RuntimeException {
    private final HttpStatus statusCode;

    private final String description;

    public LinkNotFoundException(Long id, String link) {
        super("Ссылка %s для чата с id %d не найдена.".formatted(link, id));
        this.statusCode = HttpStatus.NOT_FOUND;
        this.description = "Сылка не найдена.";
    }
}
