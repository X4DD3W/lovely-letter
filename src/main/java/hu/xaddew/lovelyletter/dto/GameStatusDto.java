package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játék státusz adatmodell")
public class GameStatusDto {

  @Schema(description = "Soron lévő játékos neve")
  private String actualPlayer;

  @Schema(description = "Nyilvános kártyák")
  private List<Card> publicCards;

  @Schema(description = "Játékban lévő játékosok által kijátszott kártyák")
  private List<PlayerAndPlayedCardsDto> playedCardsByPlayersInGame;

  @Schema(description = "Kiesett játkosok által kijátszott kártyák")
  private List<PlayerAndPlayedCardsDto> playedCardsByPlayersOutOfGame;

  @Schema(description = "Kártyák száma a húzópakliban")
  private Integer numberOfCardsInDrawDeck;

  @Schema(description = "Nyilvános eseménynapló")
  private List<String> log;
}
