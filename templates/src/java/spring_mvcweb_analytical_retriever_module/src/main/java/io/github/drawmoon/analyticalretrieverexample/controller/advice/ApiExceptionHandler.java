package io.github.drawmoon.analyticalretrieverexample.controller.advice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

  @Data
  @EqualsAndHashCode(callSuper = false)
  @Builder(toBuilder = true)
  static class ExceptionResponse extends BaseModel {
    private String error;

    @JsonProperty("timestamp")
    public String getTimestamp() {
      return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException ex) {
    String errorMessage =
        String.format("Error: %s - %s", ex.getClass().getSimpleName(), ex.getMessage());

    log.error("Error: {}", errorMessage, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ExceptionResponse.builder().error(errorMessage).build());
  }
}
