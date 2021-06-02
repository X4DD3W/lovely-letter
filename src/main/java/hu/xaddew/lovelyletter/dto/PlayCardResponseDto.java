package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayCardResponseDto {

  private String message;
  private String lastLog;

  public PlayCardResponseDto() {
    this.message = "";
    this.lastLog = "";
  }
}
