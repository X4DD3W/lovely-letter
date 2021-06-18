package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.CustomCard;
import java.util.List;

public interface CustomCardService {

  List<CustomCard> findAll();

  List<CardResponseDto> getAllCards();
}
