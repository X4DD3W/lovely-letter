package hu.xaddew.lovelyletter.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerAndPlayedCardsDto {

  private String playerName;
  private List<String> playedCards;
}
