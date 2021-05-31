package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.repository.CardRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final ModelMapper modelMapper;
  private final CardRepository cardRepository;

  @Override
  public List<CardResponseDto> getAllCards() {
    Set<String> set = new HashSet<>();
    List<Card> distinctCards = cardRepository.findAll()
        .stream()
        .filter(e -> set.add(e.getCardName()))
        .collect(Collectors.toList());

    List<CardResponseDto> dtos = new ArrayList<>();
    distinctCards.forEach(card -> {
      CardResponseDto dto = modelMapper.map(card, CardResponseDto.class);
      dtos.add(dto);
    });

    return dtos;
  }

  @Override
  public List<Card> findAll() {
    return cardRepository.findAll();
  }

  @Override
  public Card findCardByCardName(String cardName) {
    return cardRepository.findFirstByCardName(cardName);
  }
}
