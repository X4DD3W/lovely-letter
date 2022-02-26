package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.domain.Card;
import hu.xaddew.lovelyletter.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

  public Card getCardAtPlayerByCardName(Player actualPlayer, String cardName) {
    return actualPlayer.getCardsInHand().stream()
        .filter(card -> card.getName().equals(cardName))
        .findFirst().orElse(null);
  }
}
