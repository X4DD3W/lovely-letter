package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.initGames;
import static util.LLTestUtils.numberOfPreGeneratedGames;

import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
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

  @BeforeAll
  static void init() {
    games = initGames(numberOfPreGeneratedGames);
  }

  @Test
  void testInitialization() {
    assertEquals(numberOfPreGeneratedGames, games.size());
  }

 /* @Test
  void getAllGamesWithSecretInfos() {
    when(gameRepository.findAll()).thenReturn(null);
  }*/
}
