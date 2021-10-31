package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Kártya visszatétele a húzópakliba válasz adatmodell")
public class ReturnCardResponseDto {

  @Schema(description = "Titkos információ a kártyavisszatételt követően")
  private String message;

  @Schema(description = "Nyilvános eseménybejegyzés a kártyavisszatételt követően")
  private String lastLog;

  public ReturnCardResponseDto() {
    this.message = "";
    this.lastLog = "";
  }

}
