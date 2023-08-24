package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnCardResponseDto {

  private String message;
  private String lastLog;

  public ReturnCardResponseDto() {
    this.message = "";
    this.lastLog = "";
  }

}
