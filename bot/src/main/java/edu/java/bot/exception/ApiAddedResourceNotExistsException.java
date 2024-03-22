package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;

public class ApiAddedResourceNotExistsException extends ApiException {
    public ApiAddedResourceNotExistsException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
