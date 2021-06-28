package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.ACTUAL_PLAYER;
import static util.LLTestUtils.FIRST_INDEX;
import static util.LLTestUtils.FOUR_PLAYERS;
import static util.LLTestUtils.INVALID_UUID;
import static util.LLTestUtils.UUID;
import static util.LLTestUtils.initGames;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_GAMES;
import static util.LLTestUtils.initPlayers;

import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;
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
import util.LLTestUtils;

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
  private static List<Player> players;

  private Game resultGame;
  private List<GodModeDto> godModeDtoList;
  private GameStatusDto gameStatusDto;
  private PlayerKnownInfosDto playerKnownInfosDto;

  @BeforeAll
  static void init() {
    games = initGames(NUMBER_OF_PRE_GENERATED_GAMES);
    players = initPlayers(FOUR_PLAYERS);
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
    LLTestUtils.assertGeneratedValuesOfGamesAreEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList);
  }

  @Test
  void getAllGamesWithSecretInfosIfListIsEmpty() {
    when(gameRepository.findAll()).thenReturn(new ArrayList<>());

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertTrue(godModeDtoList.isEmpty());
  }

  @Test
  void findAll() {
    when(gameRepository.findAll()).thenReturn(games);

    List<Game> result = gameService.findAll();

    verify(gameRepository).findAll();

    assertNotNull(result);
    assertEquals(result, games);
  }

  @Test
  void findAllIfNoReturn() {
    when(gameRepository.findAll()).thenReturn(new ArrayList<>());
    List<Game> result = gameService.findAll();
    verify(gameRepository).findAll();
    assertTrue(result.isEmpty());
  }

  @Test
  void getGameStatus() {
    String gameUuid = UUID + FIRST_INDEX;
    when(gameRepository.findByUuid(gameUuid)).thenReturn(games.get(FIRST_INDEX));

    gameStatusDto = gameService.getGameStatus(gameUuid);

    verify(gameRepository).findByUuid(gameUuid);

    assertNotNull(gameStatusDto);
    assertEquals(ACTUAL_PLAYER, gameStatusDto.getActualPlayer());
  }

  @Test
  void getGameStatusThrowsGameException() {
    assertThrows(GameException.class, () -> gameService.getGameStatus(INVALID_UUID));
  }

  @Test
  void getAllInfosByPlayerUuid() {
    String playerUuid = UUID + FIRST_INDEX;
    when(playerRepository.findByUuid(playerUuid)).thenReturn(players.get(FIRST_INDEX));
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(games.get(FIRST_INDEX));

    playerKnownInfosDto = gameService.getAllInfosByPlayerUuid(playerUuid);

    verify(playerRepository).findByUuid(playerUuid);
    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNotNull(playerKnownInfosDto);
  }

  @Test
  void getAllInfosByPlayerUuidThrowsGameException() {
    assertThrows(GameException.class, () -> gameService.getAllInfosByPlayerUuid(INVALID_UUID));
  }

  @Test
  void findGameByPlayerUuid() {
    String playerUuid = UUID + FIRST_INDEX;
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(games.get(FIRST_INDEX));

    resultGame = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNotNull(resultGame);
    assertEquals(resultGame, games.get(FIRST_INDEX));
  }

  @Test
  void findGameByPlayerUuidIfNoReturn() {
    String playerUuid = UUID + FIRST_INDEX;
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(null);

    resultGame = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNull(resultGame);
  }

  // TODO test: createGame, playCard, putBackCards


}
