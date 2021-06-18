package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.NewReleaseCard;
import java.util.List;

public interface NewReleaseCardService {

  List<NewReleaseCard> findAll();

  List<CardResponseDto> getAllCards();

}
