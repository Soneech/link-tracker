package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;


public class BadRequestException extends ApiException {

    public BadRequestException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
