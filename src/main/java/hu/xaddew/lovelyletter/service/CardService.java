package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.model.Card;

public interface CardService {

  Card findCardByCardName(String cardName);

  void save(Card card);
}
