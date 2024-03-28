package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;

public class ApiResourceUnavailableException extends ApiException {

    public ApiResourceUnavailableException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
