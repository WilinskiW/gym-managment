package com.task.gymmanagement.infrastructure;

import com.task.gymmanagement.domain.exception.GymNotFoundException;
import com.task.gymmanagement.infrastructure.dto.GymNotFoundExceptionResponseDto;
import com.task.gymmanagement.infrastructure.dto.ValidationErrorResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    private static final String DEFAULT_VALIDATION_MESSAGE = "Validation failed";
    private static final String JSON_PARSE_ERROR_MESSAGE = "Invalid JSON format or value";
    private static final String VALIDATION_ERROR_LOG_MESSAGE = "Validation error: {}";
    private static final String JSON_PARSE_LOG_MESSAGE = "JSON parse error: {}";
    private static final String ILLEGAL_ARGUMENT_LOG_MESSAGE = "Illegal argument: {}";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        var message = fieldError != null ? fieldError.getDefaultMessage() : DEFAULT_VALIDATION_MESSAGE;

        log.warn(VALIDATION_ERROR_LOG_MESSAGE, message);
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.warn(JSON_PARSE_LOG_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value(), JSON_PARSE_ERROR_MESSAGE));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn(ILLEGAL_ARGUMENT_LOG_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(GymNotFoundException.class)
    public ResponseEntity<GymNotFoundExceptionResponseDto> handleGymNotFoundException(GymNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(new GymNotFoundExceptionResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
