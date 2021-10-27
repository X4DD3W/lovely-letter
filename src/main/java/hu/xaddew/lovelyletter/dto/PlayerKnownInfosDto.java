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
@Schema(description = "Játákos rendelkezésére álló nyilvános információk adatmodell")
public class PlayerKnownInfosDto {

  @Schema(description = "Játékos neve")
  private String myName;

  @Schema(description = "Levelek száma")
  private Integer numberOfLetters;

  @Schema(description = "Játákos kezében lévő kártyák listája")
  private List<Card> cardsInHand;

  @Schema(description = "Összes játékos által kijátszott kártyák listája")
  private List<Card> playedCards;

  @Schema(description = "A játékos nevét tartalmazó, nyilvános eseménynapló-bejegyzések listája")
  private List<String> gameLogsAboutMe;

  @Schema(description = "A játékos nevét tartalmazó, rejtett eseménynapló-bejegyzések listája")
  private List<String> gameHiddenLogsAboutMe;

  @Schema(description = "Nyilvános eseménynapló")
  private List<String> allGameLogs;

  @Schema(description = "Többi játékos és leveleinek száma listában")
  private List<PlayerAndNumberOfLettersDto> otherPlayers;
}
