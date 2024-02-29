package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;


public class ApiBadRequestException extends ApiException {

    public ApiBadRequestException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
