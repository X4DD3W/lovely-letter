package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
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
@Tag(name = "Game")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @PostMapping("/create")
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

  @PostMapping("/play-card")
  @Operation(summary = "Kártya kijátszása kézből")
  @ApiResponse(responseCode = "200", description = "Kártyakijátszást követő log",
      content = @Content(schema = @Schema(implementation = PlayCardResponseDto.class)))
  public PlayCardResponseDto playCard(
      @Parameter(description = "Kártya kijátszása adatmodell", required = true)
      @RequestBody PlayCardRequestDto requestDto) {
    return gameService.playCard(requestDto);
  }

  @PostMapping("/put-back-cards")
  @Operation(summary = "Kártya visszatétele a húzópakliba")
  @ApiResponse(responseCode = "200", description = "Kártyavisszatételt követő log",
      content = @Content(schema = @Schema(implementation = ReturnCardResponseDto.class)))
  public ReturnCardResponseDto returnCardsToDrawDeckWithChancellor(
      @Parameter(description = "Kártya visszatétele a húzópakliba adatmodell", required = true)
      @RequestBody ReturnCardsRequestDto requestDto) {
    return gameService.returnCardsToDrawDeck(requestDto);
  }

}
