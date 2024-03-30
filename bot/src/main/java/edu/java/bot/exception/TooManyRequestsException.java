package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;

public class TooManyRequestsException extends ApiException {
    public TooManyRequestsException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
