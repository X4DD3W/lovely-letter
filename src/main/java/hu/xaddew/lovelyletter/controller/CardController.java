package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.dto.CardResponseDto;
import hu.xaddew.lovelyletter.service.CustomCardService;
import hu.xaddew.lovelyletter.service.NewReleaseCardService;
import hu.xaddew.lovelyletter.service.OriginalCardService;
import hu.xaddew.lovelyletter.util.AllowCrossOriginPort4200;
import hu.xaddew.lovelyletter.util.DefaultApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllowCrossOriginPort4200
@DefaultApiErrorResponses
@RequestMapping("/card")
@Tag(name = "Cards")
@RequiredArgsConstructor
public class CardController {

  private final OriginalCardService originalCardService;
  private final NewReleaseCardService newReleaseCardService;
  private final CustomCardService customCardService;

  @GetMapping("/original-cards")
  @Operation(summary = "Lists all the cards from the original version.")
  @ApiResponse(responseCode = "200", description = "List of the original cards",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getOriginalCards() {
    return originalCardService.getAllCards();
  }

  @GetMapping("/2019-cards")
  @Operation(summary = "Lists all the cards from the 2019 edition of the game.")
  @ApiResponse(responseCode = "200", description = "List of the new edition cards",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getNewReleaseCards() {
    return newReleaseCardService.getAllCards();
  }

  @GetMapping("/custom-cards")
  @Operation(summary = "Lists all the custom cards.")
  @ApiResponse(responseCode = "200", description = "List of the custom cards",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getCustomCards() {
    return customCardService.getAllCards();
  }

}
