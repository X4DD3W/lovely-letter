package hu.xaddew.lovelyletter.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Hibaüzenet adatmodell")
public class ErrorResponse {

  @Schema(description = "Hibaüzenet")
  private String errorMessage;

  @Schema(description = "Hibakód")
  private Integer errorCode;

  @Schema(description = "Hiba alkód")
  private Integer errorSubCode;

  @Schema(description = "A hiba felületen megjelenő címe")
  private String userTitle;

  @Schema(description = "A hiba felületen megjelenő szövege")
  private String userMessage;

  @Schema(description = "Tranzakcíós azonosító")
  private String transactionId;

  @Schema(description = "Node azonosító")
  private String nodeId;

}
