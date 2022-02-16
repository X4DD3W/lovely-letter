package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Player;

public interface CardService {

  Card getCardAtPlayerByCardName(Player actualPlayer, String cardName);
}
