package util;

import hu.xaddew.lovelyletter.model.OriginalCard;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LLTestUtils {

  public static List<OriginalCard> initOriginalCards(int numberOfCards) {
    List<OriginalCard> cards = new ArrayList<>();
    for (int i = 1; i <= numberOfCards; i++) {
      cards.add(OriginalCard.builder()
          .cardName("Teszt" + i)
          .cardValue(numberOfCards - i)
          .quantity(i)
          .description("Description" + i)
          .isAtAPlayer(false)
          .isPutAside(false)
          .is2PlayerPublic(false)
          .build());
    }
    return cards;
  }
}
