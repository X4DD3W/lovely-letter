package hu.xaddew.lovelyletter.exception;

import hu.xaddew.lovelyletter.enums.ErrorMessage;
import hu.xaddew.lovelyletter.enums.ErrorType;

public class GameException extends BaseServiceException {

  public GameException(ErrorMessage errorMessage, ErrorType errorType) {
    super(errorMessage, errorType);
  }

  public GameException(ErrorMessage errorMessage, String message, ErrorType errorType) {
    super(errorMessage, message, errorType);
  }
}
