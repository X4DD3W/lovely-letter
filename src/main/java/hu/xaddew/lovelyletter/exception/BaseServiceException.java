package hu.xaddew.lovelyletter.exception;

import lombok.Getter;

@Getter
public class BaseServiceException extends RuntimeException {

  private static final long serialVersionUID = 6306927885037698016L;

  private final ErrorType errorType;

  public BaseServiceException(String message) {
    this(message, ErrorType.INTERNAL_SERVER_ERROR);
  }

  public BaseServiceException(String message, ErrorType errorType) {
    super(message);
    this.errorType = errorType;
  }

}
