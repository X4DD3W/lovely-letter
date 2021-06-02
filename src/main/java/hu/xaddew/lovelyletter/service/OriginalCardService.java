package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.OriginalCard;
import java.util.List;

public interface OriginalCardService {

  List<OriginalCard> findAll();

  List<CardResponseDto> getAllCards();
}
