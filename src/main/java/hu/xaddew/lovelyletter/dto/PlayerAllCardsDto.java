package hu.xaddew.lovelyletter.dto;

import hu.xaddew.lovelyletter.model.Card;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerAllCardsDto {

  private List<Card> cardsInHand;
  private List<Card> playedCards;
}
