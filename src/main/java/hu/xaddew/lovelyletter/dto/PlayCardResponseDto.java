package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayCardResponseDto {

  private String hiddenMessage;
  private String lastLog;

  public PlayCardResponseDto() {
    this.hiddenMessage = "";
    this.lastLog = "";
  }
}
