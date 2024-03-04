package edu.java.exception;

import edu.java.dto.api.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiErrorResponseException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;
}
