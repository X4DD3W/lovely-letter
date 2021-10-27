package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Kártya adatmodell")
public class CardResponseDto {

  @Schema(description = "Kártya neve")
  private String cardName;

  @Schema(description = "Kártya neve angolul")
  private String cardNameEnglish;

  @Schema(description = "Kártya értéke")
  private Integer cardValue;

  @Schema(description = "Kártya darabszáma a pakliban")
  private Integer quantity;

  @Schema(description = "Kártya leírása/hatása")
  private String description;
}
