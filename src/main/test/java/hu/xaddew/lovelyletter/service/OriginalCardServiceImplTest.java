package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.repository.OriginalCardRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import util.LLTestUtils;

@ExtendWith(MockitoExtension.class)
class OriginalCardServiceImplTest {

  @Spy
  private ModelMapper modelMapper;

  @Mock
  private OriginalCardRepository originalCardRepository;

  @InjectMocks
  private OriginalCardServiceImpl originalCardService;

  private static final int numberOfPreGeneratedCards = 10;
  private static List<OriginalCard> cards;
  private static List<CardResponseDto> resultCards;

  @BeforeAll
  static void init() {
    cards = LLTestUtils.initOriginalCards(numberOfPreGeneratedCards);
  }

  @Test
  void testInitialization() {
    assertEquals(numberOfPreGeneratedCards, cards.size());
  }

  @Test
  void getAllCardsWith10Cards() {
    when(originalCardRepository.findAll()).thenReturn(cards);

    resultCards = originalCardService.getAllCards();

    verify(originalCardRepository).findAll();
    verify(modelMapper, times(numberOfPreGeneratedCards)).map(any(), eq(CardResponseDto.class));

    assertEquals(numberOfPreGeneratedCards, resultCards.size());
  }

  @Test
  void getAllCardsIfNoDatabase() {
    when(originalCardRepository.findAll()).thenReturn(new ArrayList<>());

    resultCards = originalCardService.getAllCards();

    verify(originalCardRepository).findAll();
    verify(modelMapper, times(0)).map(any(), eq(CardResponseDto.class));

    assertEquals(0, resultCards.size());
  }
}
