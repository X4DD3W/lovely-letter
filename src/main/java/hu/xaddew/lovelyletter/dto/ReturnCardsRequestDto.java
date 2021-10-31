package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Kártya visszatétele a húzópakliba kérés adatmodell")
public class ReturnCardsRequestDto {

  @Schema(description = "Játékos uuid")
  private String playerUuid;

  @Schema(description = "Visszatenni kívánt kártyák nevének listája")
  private List<String> cardsToReturn;
}
