package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CreateGameRequestDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.ReturnCardResponseDto;
import hu.xaddew.lovelyletter.dto.ReturnCardsRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.service.GameService;
import hu.xaddew.lovelyletter.util.AllowCrossOriginPort4200;
import hu.xaddew.lovelyletter.util.DefaultApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllowCrossOriginPort4200
@DefaultApiErrorResponses
@RequestMapping("/game")
@Tag(name = "Games")
@RequiredArgsConstructor
public class GameController {

/*
TODO language: add english descriptions to log messages and cards (see: ErrorMessage, GameLog, GameLogService,
 GameService and data.sql

TODO dependencies: check vulnerabilities

TODO refactor: (only hungarian)
   ? GameServiceImpl 1000 sor...
   ? 2019-es verzió: Kancellár miatt a "drawDeck" LinkedList kell, hogy legyen! (Hibernate tudja?)

TODO feature: (only hungarian)
   ! kártyák rendszerét refactorálni? (kártya típusa legyen enum)
   ! CustomCard.class: CardPack enum ("Furcsa alakok", "A káosz egy létra" stb.)
     ~ "Furcsa alakok" és logikájuk
       + Kili (kész, tesztet írni)
       ! Vándorszínész
       ! Dalnok
       ! Orvosdoktor
     ! "A káosz egy létra" és logikájuk
       ! Paplovag stb...

  TODO frontend:
      ? React?
*/

  private final GameService gameService;

  @PostMapping("/create")
  @Operation(summary = "Create new game")
  @ApiResponse(responseCode = "200", description = "Created games info: uuid and players",
      content = @Content(schema = @Schema(implementation = CreatedGameResponseDto.class)))
  public CreatedGameResponseDto createGame(
      @Parameter(description = "Game creation", required = true)
      @RequestBody CreateGameRequestDto createGameDto) {
    return gameService.createGame(createGameDto);
  }

  @GetMapping("/god-mode")
  @Operation(summary = "All existing game data (with hidden information)")
  @ApiResponse(responseCode = "200", description = "List of all the games with hidden information",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = GodModeDto.class))))
  public List<GodModeDto> getAllGames() {
    return gameService.getAllGamesWithSecretInfos();
  }

  @GetMapping("/get-status/{gameUuid}")
  @Operation(summary = "Get game status by gameUuid")
  @ApiResponse(responseCode = "200", description = "Status of the game",
      content = @Content(schema = @Schema(implementation = GameStatusDto.class)))
  public GameStatusDto getGameStatus(
      @Parameter(description = "Game uuid", required = true) @PathVariable String gameUuid) {
    return gameService.getGameStatus(gameUuid);
  }

  @PostMapping("/play-card")
  @Operation(summary = "Playing card form hand")
  @ApiResponse(responseCode = "200", description = "Log after playing card from hand",
      content = @Content(schema = @Schema(implementation = PlayCardResponseDto.class)))
  public PlayCardResponseDto playCard(
      @Parameter(description = "Playing card", required = true)
      @RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @PostMapping("/put-back-cards")
  @Operation(summary = "Put card back to the draw deck")
  @ApiResponse(responseCode = "200", description = "Log after putting card back to the draw deck",
      content = @Content(schema = @Schema(implementation = ReturnCardResponseDto.class)))
  public ReturnCardResponseDto returnCardsToDrawDeckWithChancellor(
      @Parameter(description = "Putting card back to the draw deck", required = true)
      @RequestBody ReturnCardsRequestDto requestDto) {
    return gameService.returnCardsToDrawDeck(requestDto);
  }

}
