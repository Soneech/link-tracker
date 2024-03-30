package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;

public class ResourceUnavailableException extends ApiException {

    public ResourceUnavailableException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
