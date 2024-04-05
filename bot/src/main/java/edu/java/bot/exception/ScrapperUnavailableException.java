package edu.java.bot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public class ScrapperUnavailableException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    private final String message;
}
