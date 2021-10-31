package hu.xaddew.lovelyletter.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.FIRST_INDEX;
import static util.LLTestUtils.INVALID_UUID;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_GAMES;
import static util.LLTestUtils.THREE_PLAYER_NUMBER;
import static util.LLTestUtils.UUID;
import static util.LLTestUtils.initGames;

import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplsUnitTest {

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private PlayerServiceImpl playerService;

  private Game game;
  private List<Game> games;
  private List<Player> players;

  @BeforeEach
  void init() {
    games = initGames(NUMBER_OF_PRE_GENERATED_GAMES, THREE_PLAYER_NUMBER);
    game = games.get(0);
    players = games.get(0).getPlayersInGame();
  }

  @Test
  void getAllInfosByPlayerUuid() {
    String playerUuid = UUID + FIRST_INDEX;
    when(playerRepository.findByUuid(playerUuid)).thenReturn(players.get(FIRST_INDEX));

    PlayerKnownInfosDto playerKnownInfosDto = playerService.getAllInfosByPlayerUuidAndRelatedGame(playerUuid, game);

    verify(playerRepository).findByUuid(playerUuid);

    assertNotNull(playerKnownInfosDto);
  }

  @Test
  void getAllInfosByPlayerUuidThrowsGameException() {
    assertThrows(GameException.class, () -> playerService.getAllInfosByPlayerUuidAndRelatedGame(INVALID_UUID, game));
  }

}
