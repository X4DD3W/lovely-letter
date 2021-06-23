package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsRequestDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsResponseDto;
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

  // TODO
  //   + játék létrehozáskor állítható be az extra tartalom (klasszikus helyett 2019-es verzió és opcionális extra karakterek)
  //   ! 2019-es verzió:
  //      + új értékek (Hercegnő, Grófnő és Király eggyel magasabb Kancellár miatt)
  //      + új kártyák (5db): 6 - Kancellár (2),  0 - Kém (2) ÉS plusz egy Őr (összesen így 6 Őr)
  //      + változik a győzelemhez szükséges levelek száma
  //      + 6 fővel is játszható a játék
  //      ? új logikák: Kancellár miatt a "drawDeck" LinkedList kell, hogy legyen! (Hibernate nem tudja)
  //   ! Extra karakterek (és logikájuk (pl. Kili))

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
  public PlayCardResponseDto playCard(@RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @PostMapping("/put-back-cards")
  public PutBackCardsResponseDto putBackCardsWithChancellor(@RequestBody PutBackCardsRequestDto requestDto) {
    return gameService.putBackCards(requestDto);
  }

  @GetMapping("/rules")
  public ResponseEntity<Object> getRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(rulesUrl)).build();
  }
}
