package com.pfm.exception;

import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.config.MessagesProvider;
import com.pfm.filters.CorrelationIdFilter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleUnhandledException(Exception exception, WebRequest request) {
    log.error("Internal error", exception);

    String bodyOfResponse = String.format(
        MessagesProvider.getMessage(MessagesProvider.INTERNAL_ERROR),
        CorrelationIdFilter.getCorrelationId(),
        ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    );

    return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

    String bodyOfResponse = String.format(
        MessagesProvider.getMessage(MessagesProvider.BAD_REQUEST),
        CorrelationIdFilter.getCorrelationId(),
        ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    );

    return handleExceptionInternal(ex, bodyOfResponse, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    List<String> errors = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String errorMessage = error.getDefaultMessage();
      errors.add(getMessage(errorMessage));
    });
    return ResponseEntity.badRequest().body(errors);
  }
}
