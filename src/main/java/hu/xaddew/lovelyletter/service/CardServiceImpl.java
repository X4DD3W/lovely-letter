package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;

  @Override
  public Card findCardByCardName(String cardName) {
    return cardRepository.findFirstByCardName(cardName);
  }

  @Override
  public void save(Card card) {
    cardRepository.save(card);
  }
}
