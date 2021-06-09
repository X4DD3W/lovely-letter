package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerKnownInfosDto {

  private String myName;
  private Integer numberOfLetters;
  private List<Card> cardsInHand;
  private List<Card> playedCards;
  private List<String> gameLogsAboutMe;
  private List<String> gameHiddenLogsAboutMe;
  private List<String> allGameLogs;
  private List<PlayerAndNumberOfLettersDto> otherPlayers;
}
