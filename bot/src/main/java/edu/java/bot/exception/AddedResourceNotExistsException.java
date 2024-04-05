package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;

public class AddedResourceNotExistsException extends ApiException {
    public AddedResourceNotExistsException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
