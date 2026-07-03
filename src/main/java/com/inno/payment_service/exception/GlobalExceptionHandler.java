package com.inno.payment_service.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inno.payment_service.dto.response.ErrorResponseDto;
import com.inno.payment_service.dto.response.ValidationErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private static ErrorResponseDto getResponseBody (String errorMessage, int status) throws JsonProcessingException {
        return ErrorResponseDto.builder()
                .dateTime("UTC: " + formatter.format(Instant.now().atZone(ZoneId.of("UTC"))))
                .description(errorMessage)
                .status(status)
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> badRequestExceptionHandler (BadRequestException e)
            throws JsonProcessingException {
        log.error("Exception: BadRequestException. " +
                "Exception message: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getResponseBody(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> notFoundExceptionHandler (NotFoundException e)
            throws JsonProcessingException {
        log.error("Exception: NotFoundException. " +
                "Exception message: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(getResponseBody(e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> orderNotFoundExceptionHandler (OrderNotFoundException e)
            throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(getResponseBody(e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }


    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponseDto> serverExceptionHandler(ServerException e)
            throws JsonProcessingException {
        log.error("Exception: ServerException. " +
                "Exception message: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getResponseBody(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(MyValidationException.class)
    public ResponseEntity<ErrorResponseDto> validationExceptionHandler (
            MyValidationException e) throws JsonProcessingException{
        log.error("Exception: MyValidationException. " +
                "Exception message: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getResponseBody(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> ConstraintViolationExceptionHandler (
            ConstraintViolationException e) throws JsonProcessingException{
        log.error("Exception: ConstraintViolationException. " +
                "Exception message: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getResponseBody(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(HttpServletRequest httpServletRequest) throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(getResponseBody("Authentication required", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(HttpServletRequest httpServletRequest) throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(getResponseBody("Access Denied", HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> validationExceptionHandler (
            MethodArgumentNotValidException e) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName;
                    String errorMessage = error.getDefaultMessage();

                    if (error instanceof FieldError) {
                        fieldName = ((FieldError) error).getField();
                    } else {
                        fieldName = error.getObjectName();
                    }
                    errors.put(fieldName, errorMessage);
                });
        ValidationErrorResponseDto validErrorMessageResponseDto = ValidationErrorResponseDto.builder()
                .dateTime("UTC: " + formatter.format(Instant.now().atZone(ZoneId.of("UTC"))))
                .errorsMap(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        log.error("Exception: MethodArgumentNotValidException. " +
                "Exception message: " + errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validErrorMessageResponseDto);
    }
}
