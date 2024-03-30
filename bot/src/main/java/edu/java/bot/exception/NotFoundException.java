package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;


public class NotFoundException extends ApiException {
    public NotFoundException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
