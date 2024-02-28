package edu.java.controller;

import edu.java.dto.response.ApiErrorResponse;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.TelegramChatAlreadyExistsException;
import edu.java.exception.TelegramChatNotFoundException;
import edu.java.util.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyExistsException(TelegramChatAlreadyExistsException e) {
        var response = new ApiErrorResponse(
            e.getDescription(),
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleInvalidArgumentTypeException(MethodArgumentTypeMismatchException e) {
        var response = new ApiErrorResponse(
            "Некорректные параметры запроса.",
            HttpStatus.BAD_REQUEST.toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleChatNotFoundException(TelegramChatNotFoundException e) {
        var response = new ApiErrorResponse(
            e.getDescription(),
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyAddedException(LinkAlreadyAddedException e) {
        var response = new ApiErrorResponse(
            e.getDescription(),
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleLinkNotFoundException(LinkNotFoundException e) {
        var response = new ApiErrorResponse(
            e.getDescription(),
            e.getStatusCode().toString(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            StackTraceUtil.getStackTrace(e)
        );
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
}
