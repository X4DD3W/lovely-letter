package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {

  private String message;
  private String lastLog;

  public ResponseDto() {
    this.message = "";
    this.lastLog = "";
  }
}
