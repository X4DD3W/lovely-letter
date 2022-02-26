package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
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
