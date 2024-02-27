package edu.java.bot.controller;

import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.exception.UpdateAlreadyExistsException;
import edu.java.bot.util.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleAlreadyExistsException(UpdateAlreadyExistsException e) {
        return new ApiErrorResponse(
            e.getDescription(),
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidArgumentsException(MethodArgumentNotValidException e) {
        return new ApiErrorResponse(
            "Ошибка при валидации параметров запроса.",
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            "Некорректные параметры запроса.",
            e.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage()).toList()
        );
    }
}
