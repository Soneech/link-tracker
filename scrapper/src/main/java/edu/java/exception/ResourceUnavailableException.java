package edu.java.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public class ResourceUnavailableException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;
}
