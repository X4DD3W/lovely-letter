package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.repository.OriginalCardRepository;
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
public class OriginalCardService {

  private final ModelMapper modelMapper;
  private final OriginalCardRepository originalCardRepository;

  public List<OriginalCard> findAll() {
    return originalCardRepository.findAll();
  }

  public List<CardResponseDto> getAllCards() {
    Set<String> set = new HashSet<>();
    List<OriginalCard> distinctCards = originalCardRepository.findAll()
        .stream()
        .filter(e -> set.add(e.getCardName()))
        .collect(Collectors.toList());

    List<CardResponseDto> dtos = new ArrayList<>();
    distinctCards.forEach(card -> dtos.add(modelMapper.map(card, CardResponseDto.class)));

    return dtos;
  }
}
