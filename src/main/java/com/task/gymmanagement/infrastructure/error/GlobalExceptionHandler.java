package com.task.gymmanagement.infrastructure.error;

import com.task.gymmanagement.domain.exception.GymAlreadyExistException;
import com.task.gymmanagement.domain.exception.GymNotFoundException;
import com.task.gymmanagement.domain.exception.MemberAlreadyExistsInGymException;
import com.task.gymmanagement.domain.exception.MemberNotFoundException;
import com.task.gymmanagement.domain.exception.MembershipPlanAlreadyCancelledException;
import com.task.gymmanagement.domain.exception.MembershipPlanExceedLimitException;
import com.task.gymmanagement.domain.exception.MembershipPlanNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String DEFAULT_VALIDATION_MESSAGE = "Validation failed";
    private static final String JSON_PARSE_ERROR_MESSAGE = "Invalid JSON format or value";
    private static final String VALIDATION_ERROR_LOG_MESSAGE = "Validation error at fields: {}";
    private static final String JSON_PARSE_LOG_MESSAGE = "JSON parse error: {}";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        log.error(DEFAULT_ERROR_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), DEFAULT_ERROR_MESSAGE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn(VALIDATION_ERROR_LOG_MESSAGE, errors.keySet());
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value(), DEFAULT_VALIDATION_MESSAGE, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.warn(JSON_PARSE_LOG_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), JSON_PARSE_ERROR_MESSAGE));
    }

    @ExceptionHandler({GymNotFoundException.class, MemberNotFoundException.class, MembershipPlanNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MembershipPlanExceedLimitException.class)
    public ResponseEntity<ErrorResponseDto> handleMembershipPlanExceedLimitException(RuntimeException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MembershipPlanAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponseDto> handleMembershipPlanAlreadyCancelledException(RuntimeException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MemberAlreadyExistsInGymException.class)
    public ResponseEntity<ErrorResponseDto> handleMemberAlreadyExistsInGymException(RuntimeException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GymAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleGymAlreadyExistException(RuntimeException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorResponseDto> handleException(RuntimeException ex, HttpStatus status) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(status.value(), ex.getMessage()), status);
    }
}
