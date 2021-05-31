package hu.xaddew.lovelyletter.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameStatusDto {

  private String actualPlayer;
  private List<PlayerAndPlayedCardsDto> playedCardsByPlayers;
  private Integer numberOfCardsInDrawDeck;
  private List<String> log;
}
