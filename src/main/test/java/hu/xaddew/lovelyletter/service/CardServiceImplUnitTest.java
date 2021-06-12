package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static util.LLTestUtils.CARD_NAME;
import static util.LLTestUtils.initCard;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceImplUnitTest {

  @InjectMocks
  private CardServiceImpl cardService;

  private static Card card;
  private static Player player;

  @BeforeAll
  static void init() {
    player = new Player();
    card = initCard();
    player.getCardsInHand().add(card);
  }

  @Test
  void getCardAtPlayerByCardName() {
    Card result = cardService.getCardAtPlayerByCardName(player, CARD_NAME);
    assertEquals(card, result);
  }

  @Test
  void getCardAtPlayerByCardNameIfActualPlayerHasNoCardInHand() {
    assertNull(cardService.getCardAtPlayerByCardName(new Player(), CARD_NAME));
  }

  @Test
  void getCardAtPlayerByCardNameIfCardNameIsNull() {
    Card result = cardService.getCardAtPlayerByCardName(player, null);
    assertNull(result);
  }

  @Test
  void getCardAtPlayerByCardNameIfCardNameIsNotFound() {
    Card result = cardService.getCardAtPlayerByCardName(player, "SomethingNotExists");
    assertNull(result);
  }
}
