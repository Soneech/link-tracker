package edu.java.controller;

import edu.java.dto.api.response.ApiErrorResponse;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.ResourceNotExistsException;
import edu.java.exception.ResourceUnavailableException;
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
            "Chat id должно иметь тип int64.",
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

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleResourceNotExistsException(ResourceNotExistsException e) {
        var response = new ApiErrorResponse(
            "Ресурс не найден.",
            HttpStatus.I_AM_A_TEAPOT.toString(),
            e.getClass().getSimpleName(),
            "Ссылка на несуществующий ресурс, либо ресурс был удалён.",
            StackTraceUtil.getStackTrace(e)
        );

        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(response);  // временно
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleResourceUnavailableException(ResourceUnavailableException e) {
        var response = new ApiErrorResponse(
            "Ресурс недоступен",
            e.getHttpStatusCode().toString(),
            e.getClass().getSimpleName(),
            "Ресурс временно недоступен",
            StackTraceUtil.getStackTrace(e)
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
