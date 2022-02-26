package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.CARD_DESCRIPTION;
import static util.LLTestUtils.CARD_NAME;
import static util.LLTestUtils.initOriginalCards;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.domain.OriginalCard;
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

@ExtendWith(MockitoExtension.class)
class OriginalCardServiceUnitTest {

  @Spy
  private ModelMapper modelMapper;

  @Mock
  private OriginalCardRepository originalCardRepository;

  @InjectMocks
  private OriginalCardService originalCardService;

  private static List<OriginalCard> originalCards;
  private static List<CardResponseDto> resultCards;

  @BeforeAll
  static void init() {
    originalCards = initOriginalCards(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS);
  }

  @Test
  void testInitialization() {
    assertEquals(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS, originalCards.size());
  }

  @Test
  void getAllCards() {
    when(originalCardRepository.findAll()).thenReturn(originalCards);

    resultCards = originalCardService.getAllCards();

    verify(originalCardRepository).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS)).map(any(), eq(CardResponseDto.class));

    assertEquals(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS, resultCards.size());
    assertGeneratedValuesAreEquals(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS, resultCards);
  }

  @Test
  void getAllCardsIfNoDatabase() {
    when(originalCardRepository.findAll()).thenReturn(new ArrayList<>());

    resultCards = originalCardService.getAllCards();

    verify(originalCardRepository).findAll();
    verify(modelMapper, times(0)).map(any(), eq(CardResponseDto.class));

    assertEquals(0, resultCards.size());
  }


  private void assertGeneratedValuesAreEquals(int numberOfCards, List<CardResponseDto> resultCards) {
    for (int i = 1; i <= numberOfCards ; i++) {
      CardResponseDto actualCard = resultCards.get(i - 1);
      assertEquals(CARD_NAME + i, actualCard.getName());
      assertEquals(numberOfCards - i, actualCard.getValue());
      assertEquals(i, actualCard.getQuantity());
      assertEquals(CARD_DESCRIPTION + i, actualCard.getDescription());
    }
  }
}
