package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.service.GameService;
import hu.xaddew.lovelyletter.service.PlayerService;
import hu.xaddew.lovelyletter.util.AllowCrossOriginPort4200;
import hu.xaddew.lovelyletter.util.DefaultApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllowCrossOriginPort4200
@DefaultApiErrorResponses
@RequestMapping("/player")
@Tag(name = "Játékosokkal kapcsolatos végpontok")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;
  private final GameService gameService;

  @GetMapping("/known-infos-by-player/{playerUuid}")
  @Operation(summary = "Játékos rendelkezésére álló nyilvános információk lekérdezése playerUuid alapján")
  @ApiResponse(responseCode = "200", description = "Játékos rendelkezésére álló nyilvános információk",
      content = @Content(schema = @Schema(implementation = PlayerKnownInfosDto.class)))
  public PlayerKnownInfosDto getCardsByPlayerUuid(
      @Parameter(description = "Játékos uuid", required = true) @PathVariable String playerUuid) {
    Game game = gameService.findGameByPlayerUuid(playerUuid);
    return playerService.getAllInfosByPlayerUuidAndRelatedGame(playerUuid, game);
  }

}