package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.Card;
import java.util.List;

public interface CardService {

  List<CardResponseDto> getAllCards();

  List<Card> findAll();

  Card findCardByCardName(String cardName);
}
