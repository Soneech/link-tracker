package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;


public class ApiNotFoundException extends ApiException {
    public ApiNotFoundException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse);
    }
}
