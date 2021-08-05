package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.ResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsRequestDto;
import hu.xaddew.lovelyletter.service.CustomCardService;
import hu.xaddew.lovelyletter.service.GameService;
import hu.xaddew.lovelyletter.service.NewReleaseCardService;
import hu.xaddew.lovelyletter.service.OriginalCardService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:4200" })
public class MainController {

  @Value("${lovely-letter-rules-url}")
  private String rulesUrl;

  private final OriginalCardService originalCardService;
  private final NewReleaseCardService newReleaseCardService;
  private final CustomCardService customCardService;
  private final GameService gameService;

  // TODO tesztelni/javítani:
  //  ? Hibernate OneToMany (gameId-val összekapcsolás refactor)
  //  ? 2019-es verzió: Kancellár miatt a "drawDeck" LinkedList kell, hogy legyen! (Hibernate tudja?)
  // tesztek: refactor, összevonni parameterized-dá

  // TODO feature:
  //  ! Extra karakterek (és logikájuk (pl. Kili))
  //  ! CustomCard.class: CardPack enum ("Furcsa alakok", "A káosz egy létra" stb.)
  //  ! Slf4j logolás (FE teszteléshez)

  // TODO extra
  //  ! scheduler: ha egy játéknál 1 órája nem történt változás, zárja le.
  //  ! scheduler: 3 hónap után törölje a lezárt játékokat.

  @GetMapping("/cards/original")
  public List<CardResponseDto> getOriginalCards() {
    return originalCardService.getAllCards();
  }

  @GetMapping("/cards/2019")
  public List<CardResponseDto> getNewReleaseCards() {
    return newReleaseCardService.getAllCards();
  }

  @GetMapping("/cards/custom")
  public List<CardResponseDto> getCustomCards() {
    return customCardService.getAllCards();
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
  public ResponseDto playCard(@RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @PostMapping("/put-back-cards")
  public ResponseDto putBackCardsWithChancellor(@RequestBody PutBackCardsRequestDto requestDto) {
    return gameService.putBackCards(requestDto);
  }

  @GetMapping("/rules")
  public ResponseEntity<Object> getRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(rulesUrl)).build();
  }
}
