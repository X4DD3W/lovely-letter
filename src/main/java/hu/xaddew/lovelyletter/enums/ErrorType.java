package hu.xaddew.lovelyletter.enums;

import lombok.Getter;

@Getter
public enum ErrorType {

  NO_CONTENT(204),
  BAD_REQUEST(400),
  NOT_FOUND(404),
  CONFLICT(409),
  PRECONDITION_FAILED(412),
  INTERNAL_SERVER_ERROR(500);

  ErrorType(int statusCode) {
    this.statusCode = statusCode;
  }

  private final int statusCode;
}
