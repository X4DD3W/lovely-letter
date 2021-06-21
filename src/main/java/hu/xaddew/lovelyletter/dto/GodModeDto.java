package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GodModeDto {

  private Long id;
  private String uuid;
  private Card putAsideCard;
  private List<Card> publicCards;
  private List<Card> drawDeck;
  private List<Player> playersInGame;
  private String actualPlayer;
  private List<String> log;
  private List<String> hiddenLog;
  private Boolean isGameOver;
  private Boolean is2019Version;
}
