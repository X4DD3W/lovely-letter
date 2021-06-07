package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.service.GameService;
import hu.xaddew.lovelyletter.service.OriginalCardService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  // FIXME ismert hibák, hiányosságok, bővíthetőségek:
  //   - refactor: kiszervezni a kártyalogikákat methodokba
  //   - "playedCards" LinkedList kell, hogy legyen valahogy (Hibernate nem tudja)

  // TODO
  //   - játék létrehozáskor állítható be az extra tartalom (2019-es verzió és extra karakterek)
  //   - 2019-es verzió:
  //      - új kártyák: 6 - Kancellár (2),  0 - Kém (2) ÉS plusz egy Őr (összesen így 6)!
  //      - változik a győzelemhez szükséges levelek száma
  //      - 6 fővel is játszható a játék
  //      - Kancellár miatt a "drawDeck" LinkedList kell, hogy legyen
  //   - Extra karakterek (és logikájuk (pl. Kili))

  @GetMapping("/cards")
  public List<CardResponseDto> getCards() {
    return originalCardService.getAllCards();
  }

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

  @GetMapping("/my-known-infos/{playerUuid}")
  public PlayerKnownInfosDto getCardsByPlayerUuid(@PathVariable String playerUuid) {
    return gameService.getAllInfosByPlayerUuid(playerUuid);
  }

  @PostMapping("/play-card")
  public PlayCardResponseDto playCard(@RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @GetMapping("/rules")
  public ResponseEntity<Object> getRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(
        "https://tarsasjatekok.com/files/common/2/2e/2e5/2e5de231fca31ad30447dccbc4b675b0/ll-rules-hungarian.pdf"))
        .build();
  }

}
