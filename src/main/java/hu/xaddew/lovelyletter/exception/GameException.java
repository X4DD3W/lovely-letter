package hu.xaddew.lovelyletter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class GameException extends RuntimeException {

  public GameException(String message) {
    super(message);
  }

}
