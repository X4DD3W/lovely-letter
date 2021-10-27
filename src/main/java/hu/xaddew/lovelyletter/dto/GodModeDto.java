package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Játék összes információja adatmodell")
public class GodModeDto {

  @Schema(description = "Játék azonosító")
  private Long id;

  @Schema(description = "Játék uuid")
  private String uuid;

  @Schema(description = "Félrerakott kártya")
  private Card putAsideCard;

  @Schema(description = "Nyilvános kártyák listája")
  private List<Card> publicCards;

  @Schema(description = "Húzópakliban lévő kártyák listája")
  private List<Card> drawDeck;

  @Schema(description = "Játékosok listája")
  private List<Player> playersInGame;

  @Schema(description = "Soron lévő játékos neve")
  private String actualPlayer;

  @Schema(description = "Nyilvános eseménynapló")
  private List<String> log;

  @Schema(description = "Rejtett eseménynapló")
  private List<String> hiddenLog;

  @Schema(description = "Véget ért a játék?")
  private Boolean isGameOver;

  @Schema(description = "2019-es változat?")
  private Boolean is2019Version;
}
