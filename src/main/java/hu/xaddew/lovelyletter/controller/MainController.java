package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerAllCardsDto;
import hu.xaddew.lovelyletter.service.GameService;
import hu.xaddew.lovelyletter.service.OriginalCardService;
import hu.xaddew.lovelyletter.service.PlayerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

  private final OriginalCardService originalCardService;
  private final GameService gameService;
  private final PlayerService playerService;

  // FIXME drawDeck, log, playedCards mind LinkedList, de Hibernate azt elm nem tudja! "Kancellár" és "Kém" egyelőre nincs benne a játékban.

  @GetMapping("/cards")
  public List<CardResponseDto> getCards() {
    return originalCardService.getAllCards();
  }

  // TODO normális validáció (pl. nem lehet két ugyanolyan nevű játékos egy játékon belül!)
  @PostMapping("/game/create")
  public CreatedGameResponseDto createGame(@RequestBody CreateGameDto createGameDto) {
    return gameService.createGame(createGameDto);
  }

  @GetMapping("/god-mode")
  public List<GodModeDto> getAllGames() {
    return gameService.getAllGamesWithSecretInfos();
  }

  @GetMapping("/get-status/{gameUuid}")
  public GameStatusDto getGameStatus(@PathVariable String gameUuid) {
    return gameService.getGameStatus(gameUuid);
  }

  @GetMapping("/my-cards/{playerUuid}")
  public PlayerAllCardsDto getCardsByPlayerUuid(@PathVariable String playerUuid) {
    return playerService.getAllCardsByPlayerUuid(playerUuid);
  }

  @PostMapping("/play-card")
  public PlayCardResponseDto playCard(@RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  // TODO /rules

}
