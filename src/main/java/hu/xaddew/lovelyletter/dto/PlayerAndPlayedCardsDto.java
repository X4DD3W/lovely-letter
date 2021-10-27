package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játékos és kijátszott kártyái adatmodell")
public class PlayerAndPlayedCardsDto {

  @Schema(description = "Játékos neve")
  private String playerName;

  @Schema(description = "Kijátszott kártyák nevének listája")
  private List<String> playedCards;
}
