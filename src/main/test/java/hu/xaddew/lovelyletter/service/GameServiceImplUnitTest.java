package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.ACTUAL_PLAYER;
import static util.LLTestUtils.initGames;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_GAMES;
import static util.LLTestUtils.UUID;

import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceImplUnitTest {

  @Mock
  private Random random;

  @Mock
  private CardServiceImpl cardService;

  @Mock
  private OriginalCardServiceImpl originalCardService;

  @Mock
  private PlayerServiceImpl playerService;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private GameServiceImpl gameService;

  private static List<Game> games;

  private List<GodModeDto> godModeDtoList;

  @BeforeAll
  static void init() {
    games = initGames(NUMBER_OF_PRE_GENERATED_GAMES);
  }

  @Test
  void testInitialization() {
    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, games.size());
  }

  @Test
  void getAllGamesWithSecretInfos() {
    when(gameRepository.findAll()).thenReturn(games);

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList.size());
    assertGeneratedValuesAreEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList);
  }

  @Test
  void getAllGamesWithSecretInfosIfListIsEmpty() {
    when(gameRepository.findAll()).thenReturn(new ArrayList<>());

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertTrue(godModeDtoList.isEmpty());
  }

  private void assertGeneratedValuesAreEquals(int numberOfGames, List<GodModeDto> godModeDtoList) {
    for (int i = 1; i <= numberOfGames ; i++) {
      GodModeDto actualDto = godModeDtoList.get(i - 1);
      assertEquals(i, actualDto.getId());
      assertEquals(UUID + i, actualDto.getUuid());
      assertEquals(ACTUAL_PLAYER, actualDto.getActualPlayer());
    }
  }
}
