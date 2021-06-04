package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameStatusDto {

  private String actualPlayer;
  private List<Card> publicCards;
  private List<PlayerAndPlayedCardsDto> playedCardsByPlayersInGame;
  private List<PlayerAndPlayedCardsDto> playedCardsByPlayersOutOfGame;
  private Integer numberOfCardsInDrawDeck;
  private List<String> log;
}
