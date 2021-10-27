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
import hu.xaddew.lovelyletter.util.DefaultApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@DefaultApiErrorResponses
@Tag(name = "MainController")
@CrossOrigin(origins = { "http://localhost:4200" })
public class MainController {

  @Value("${lovely-letter-rules-url}")
  private String rulesUrl;

  private final OriginalCardService originalCardService;
  private final NewReleaseCardService newReleaseCardService;
  private final CustomCardService customCardService;
  private final GameService gameService;

  @GetMapping("/cards/original")
  @Operation(summary = "Eredeti kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Eredeti kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getOriginalCards() {
    return originalCardService.getAllCards();
  }

  @GetMapping("/cards/2019")
  @Operation(summary = "Új kiadású kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Új kiadású kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getNewReleaseCards() {
    return newReleaseCardService.getAllCards();
  }

  @GetMapping("/cards/custom")
  @Operation(summary = "Egyedi kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Egyedi kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getCustomCards() {
    return customCardService.getAllCards();
  }

  @PostMapping("/game/create")
  @Operation(summary = "Új játék létrehozása")
  @ApiResponse(responseCode = "200", description = "Sikeres játék létrehozás!",
      content = @Content(schema = @Schema(implementation = CreatedGameResponseDto.class)))
  public CreatedGameResponseDto createGame(
      @Parameter(description = "Játék létrehozása adatmodell", required = true)
      @RequestBody CreateGameDto createGameDto) {
    return gameService.createGame(createGameDto);
  }

  @GetMapping("/god-mode")
  @Operation(summary = "Játékadatok lekérdezése god-módban")
  @ApiResponse(responseCode = "200", description = "Játékadatok listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = GodModeDto.class))))
  public List<GodModeDto> getAllGames() {
    return gameService.getAllGamesWithSecretInfos();
  }

  @GetMapping("/get-status/{gameUuid}")
  @Operation(summary = "Játék státuszának lekérdezése gameUuid alapján")
  @ApiResponse(responseCode = "200", description = "Játék státusza",
      content = @Content(schema = @Schema(implementation = GameStatusDto.class)))
  public GameStatusDto getGameStatus(
      @Parameter(description = "Játék uuid", required = true) @PathVariable String gameUuid) {
    return gameService.getGameStatus(gameUuid);
  }

  @GetMapping("/my-known-infos/{playerUuid}")
  @Operation(summary = "Játékos rendelkezésére álló nyilvános információk lekérdezése playerUuid alapján")
  @ApiResponse(responseCode = "200", description = "Játékos rendelkezésére álló nyilvános információk",
      content = @Content(schema = @Schema(implementation = PlayerKnownInfosDto.class)))
  public PlayerKnownInfosDto getCardsByPlayerUuid(
      @Parameter(description = "Játékos uuid", required = true) @PathVariable String playerUuid) {
    return gameService.getAllInfosByPlayerUuid(playerUuid);
  }

  @PostMapping("/play-card")
  @Operation(summary = "Kártya kijátszása kézből")
  @ApiResponse(responseCode = "200", description = "Kártyakijátszást követő log",
      content = @Content(schema = @Schema(implementation = ResponseDto.class)))
  public ResponseDto playCard(
      @Parameter(description = "Kártya kijátszása adatmodell", required = true)
      @RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @PostMapping("/put-back-cards")
  @Operation(summary = "Kártya visszatétele a húzópakliba")
  @ApiResponse(responseCode = "200", description = "Kártyakivisszatételt követő log",
      content = @Content(schema = @Schema(implementation = ResponseDto.class)))
  public ResponseDto putBackCardsWithChancellor(
      @Parameter(description = "Kártya visszatétele a húzópakliba adatmodell", required = true)
      @RequestBody PutBackCardsRequestDto requestDto) {
    return gameService.putBackCards(requestDto);
  }

  @GetMapping("/rules")
  @Operation(summary = "Szabályfüzet (pdf) lekérdezése")
  @ApiResponse(responseCode = "200", description = "Szabályfüzet pdf formátumban",
      content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
  public ResponseEntity<Object> getRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(rulesUrl)).build();
  }
}
