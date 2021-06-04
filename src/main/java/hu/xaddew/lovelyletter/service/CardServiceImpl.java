package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;

  @Override
  public void save(Card card) {
    cardRepository.save(card);
  }

  @Override
  public Card getCardAtPlayerByCardName(Player actualPlayer, String cardName) {
    return actualPlayer.getCardsInHand().stream()
        .filter(card -> card.getCardName().equals(cardName))
        .findFirst().orElse(null);
  }
}
