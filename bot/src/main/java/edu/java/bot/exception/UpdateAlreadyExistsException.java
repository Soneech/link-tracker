package edu.java.bot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UpdateAlreadyExistsException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String description;


    public UpdateAlreadyExistsException() {
        super("Обновление с таким id уже добавлено.");
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.description = "Обновление уже добавлено.";
    }
}