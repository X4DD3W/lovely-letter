package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayerAllCardsDto;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.service.CardService;
import hu.xaddew.lovelyletter.service.GameService;
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

  private final CardService cardService;
  private final GameService gameService;
  private final PlayerService playerService;

  @GetMapping("/cards")
  public List<CardResponseDto> getCards() {
    return cardService.getAllCards();
  }

  // TODO normális validáció (pl. nem lehet két ugyanolyan nevű játékos egy játékon belül!)
  @PostMapping("/game/create")
  public CreatedGameResponseDto createGame(@RequestBody CreateGameDto createGameDto) {
    return gameService.createGame(createGameDto);
  }

  @GetMapping("/god-mode")
  public List<Game> getAllGames() {
    return gameService.findAll();
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
  public String playCard(@RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }



  // TODO futó játék:
  // mindig küldöm
  // a game uuid-t (kell ez?)
  // a saját azonosítómat (player uuid) és
  // a kiválasztott lapom nevét?
  // a választott lapnak akciójának megfelelő plusz fieldet

  // TODO draw: húz egy lapot, ha én jövök

  // TODO egyéb:
  // •	2-4 játékos (Player) (a játékhoz kapott uuid-t minden kéréssel küldeni kell)
  //•	21 előre definiált kártya minden játék során
      //o	2 játékosnál 3, egyébként 1 lap „félrerakott” listába kerül
  //•	A játékos szerelmesleveleket gyűjt.
  //•	Van minden játékosnak kézben lévő lapja (lista) és maga elé eldobott lapjai (lista)
}
