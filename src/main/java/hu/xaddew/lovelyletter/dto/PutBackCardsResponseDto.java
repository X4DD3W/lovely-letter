package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutBackCardsResponseDto {

  // TODO refactor: szerkezetre ugyanaz, mint PlayCardResponseDto
  private String message;
  private String lastLog;

  public PutBackCardsResponseDto() {
    this.message = "";
    this.lastLog = "";
  }
}
