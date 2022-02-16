package hu.xaddew.lovelyletter.service.impl;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  @Override
  public Card getCardAtPlayerByCardName(Player actualPlayer, String cardName) {
    return actualPlayer.getCardsInHand().stream()
        .filter(card -> card.getName().equals(cardName))
        .findFirst().orElse(null);
  }
}
