package edu.java.bot.controller;

import edu.java.bot.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidArgumentsException(MethodArgumentNotValidException e) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса.",
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            "Ошибка при валидации параметров запроса.",
            e.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage()).toList()
        );
    }
}
