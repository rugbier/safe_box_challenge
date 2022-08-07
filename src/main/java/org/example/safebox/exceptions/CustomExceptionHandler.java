package org.example.safebox.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@ResponseBody
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(SafeboxNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage safeboxNotFoundException(SafeboxNotFoundException ex, WebRequest request) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(ExistingSafeboxException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage existingSafeboxException(ExistingSafeboxException ex, WebRequest request) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(LockedSafeboxException.class)
    @ResponseStatus(value = HttpStatus.LOCKED)
    public ErrorMessage lockedSafeboxException(LockedSafeboxException ex, WebRequest request) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.LOCKED.value())
                .timestamp(LocalDateTime.now())
                .description(request.getDescription(false))
                .build();
    }

    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage internalServerException(InternalServerError ex, WebRequest request) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .description(request.getDescription(false))
                .build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage error = new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), LocalDateTime.now(), "Malformed expected data", "Validation error");
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}
