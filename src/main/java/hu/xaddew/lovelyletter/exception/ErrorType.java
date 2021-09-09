package hu.xaddew.lovelyletter.exception;

import lombok.Getter;

@Getter
public enum ErrorType {

  BAD_REQUEST(400),
  NOT_FOUND(404),
  CONFLICT(409),
  INTERNAL_SERVER_ERROR(500);

  ErrorType(int statusCode) {
    this.statusCode = statusCode;
  }

  private final int statusCode;
}
