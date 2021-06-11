package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.CardRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import util.LLTestUtils;

@ExtendWith(MockitoExtension.class)
class CardServiceImplUnitTest {

  @Spy
  private CardRepository cardRepository;

  @InjectMocks
  private CardServiceImpl cardService;

  private static Card card;
  private static Player player;

  @BeforeAll
  static void init() {
    player = new Player();
    card = LLTestUtils.initCard();
    player.getCardsInHand().add(card);
  }

  @Test
  void getCardAtPlayerByCardName() {
    Card result = cardService.getCardAtPlayerByCardName(player, LLTestUtils.CARD_NAME);

    assertEquals(card, result);
  }

}
