package hu.xaddew.lovelyletter.exception;

import hu.xaddew.lovelyletter.enums.ErrorType;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GameServiceExceptionHandler {

  protected static final String INTERNAL_SERVER_ERROR_USER_MESSAGE =
      "Váratlan hiba történt a kérés kiszolgálása közben.";

  @ExceptionHandler({GameException.class})
  public ResponseEntity<ErrorResponse> handleException(BaseServiceException exception) {
    String userTitle = "Hiba a művelet közben!";
    String userMessage;

    switch (exception.getErrorType()) {
      case BAD_REQUEST:
      case NOT_FOUND:
      case PRECONDITION_FAILED:
      case CONFLICT:
        userMessage = exception.getMessage();
        break;
      default:
        userMessage = INTERNAL_SERVER_ERROR_USER_MESSAGE;
    }

    return getErrorResponse(userTitle, userMessage, exception);
  }

  protected ResponseEntity<ErrorResponse> getErrorResponse(String userTitle, String userMessage,
      BaseServiceException exception) {
    return getErrorResponse(userTitle, userMessage, exception.getErrorType().getStatusCode(),
        exception.getErrorType(), exception);
  }

  protected ResponseEntity<ErrorResponse> getErrorResponse(String userTitle, String userMessage,
      int statusCode, ErrorType errorType, Exception exception) {
    if (errorType == ErrorType.NO_CONTENT) {
      log.info(exception.getMessage());
    } else {
      log.error(exception.getMessage(), exception);
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .errorMessage(exception.getMessage())
        .userMessage(userMessage)
        .userTitle(userTitle)
        .transactionId(UUID.randomUUID().toString())
        .nodeId(UUID.randomUUID().toString())
        .errorCode(statusCode)
        .errorSubCode(statusCode)
        .build();

    HttpStatus status = HttpStatus.resolve(statusCode);
    assert status != null;
    return new ResponseEntity<>(errorResponse, status);
  }

}

