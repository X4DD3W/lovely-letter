package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Kártyával végzett művelet válasz adatmodell")
public class ResponseDto {

  @Schema(description = "Titkos információ a műveletet követően")
  private String message;

  @Schema(description = "Nyilvános eseménybejegyzés a műveletet követően")
  private String lastLog;

  public ResponseDto() {
    this.message = "";
    this.lastLog = "";
  }
}
