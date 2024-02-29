package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiException extends RuntimeException {
    protected final ApiErrorResponse apiErrorResponse;
}
