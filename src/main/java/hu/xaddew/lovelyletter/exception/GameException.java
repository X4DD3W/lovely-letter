package hu.xaddew.lovelyletter.exception;

public class GameException extends BaseServiceException {

  public GameException(String message, ErrorType errorType) {
    super(message, errorType);
  }
}
