package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.CustomCard;
import hu.xaddew.lovelyletter.repository.CustomCardRepository;
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
public class CustomCardService {

  private final ModelMapper modelMapper;
  private final CustomCardRepository customCardRepository;

  public List<CustomCard> findAll() {
    return customCardRepository.findAll();
  }

  public List<CardResponseDto> getAllCards() {
    Set<String> set = new HashSet<>();
    List<CustomCard> distinctCards = customCardRepository.findAll()
        .stream()
        .filter(e -> set.add(e.getCardName()))
        .collect(Collectors.toList());

    List<CardResponseDto> dtos = new ArrayList<>();
    distinctCards.forEach(card -> dtos.add(modelMapper.map(card, CardResponseDto.class)));

    return dtos;
  }
}
