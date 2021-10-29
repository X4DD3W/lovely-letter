package hu.xaddew.lovelyletter.exception;

import lombok.Getter;

@Getter
public class BaseServiceException extends RuntimeException {

  private static final long serialVersionUID = 6306927885037698016L;

  private final ErrorMessage errorMessage;
  private final ErrorType errorType;

  public BaseServiceException(ErrorMessage errorMessage, ErrorType errorType) {
    super(errorMessage.toString());
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }

  public BaseServiceException(ErrorMessage errorMessage, String message, ErrorType errorType) {
    super(errorMessage.toString() + message);
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }

}
