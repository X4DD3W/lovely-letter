package util;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.OriginalCard;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LLTestUtils {

  public static final int numberOfPreGeneratedCards = 10;
  public static final int numberOfPreGeneratedGames = 5;
  public static final String CARD_NAME = "cardName";
  public static final Integer CARD_VALUE = 100;
  public static final Integer CARD_QUANTITY = 1;
  public static final String CARD_DESCRIPTION = "Description";

  public static List<OriginalCard> initOriginalCards(int numberOfCards) {
    List<OriginalCard> cards = new ArrayList<>();
    for (int i = 1; i <= numberOfCards; i++) {
      cards.add(OriginalCard.builder()
          .cardName(CARD_NAME + i)
          .cardValue(numberOfCards - i)
          .quantity(i)
          .description(CARD_DESCRIPTION + i)
          .isAtAPlayer(false)
          .isPutAside(false)
          .is2PlayerPublic(false)
          .build());
    }
    return cards;
  }

  public static Card initCard() {
    return Card.builder()
        .cardName(CARD_NAME)
        .cardValue(CARD_VALUE)
        .quantity(CARD_QUANTITY)
        .description(CARD_DESCRIPTION)
        .build();
  }

  public static List<Game> initGames(int numberOfGames) {
    List<Game> games = new ArrayList<>();

    // TODO ittTartok

    return null;
  }
}
