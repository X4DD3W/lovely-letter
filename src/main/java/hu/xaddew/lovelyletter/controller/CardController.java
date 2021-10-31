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
@Tag(name = "Kártyákkal kapcsolatos végpontok")
@RequiredArgsConstructor
public class CardController {

  private final OriginalCardService originalCardService;
  private final NewReleaseCardService newReleaseCardService;
  private final CustomCardService customCardService;

  @GetMapping("/original-cards")
  @Operation(summary = "Eredeti kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Eredeti kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getOriginalCards() {
    return originalCardService.getAllCards();
  }

  @GetMapping("/2019-cards")
  @Operation(summary = "Új kiadású kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Új kiadású kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getNewReleaseCards() {
    return newReleaseCardService.getAllCards();
  }

  @GetMapping("/custom-cards")
  @Operation(summary = "Egyedi kártyák lekérdezése")
  @ApiResponse(responseCode = "200", description = "Egyedi kártyák listája",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
  public List<CardResponseDto> getCustomCards() {
    return customCardService.getAllCards();
  }

}
