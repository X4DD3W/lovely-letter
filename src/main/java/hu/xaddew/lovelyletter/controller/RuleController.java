package hu.xaddew.lovelyletter.controller;

import hu.xaddew.lovelyletter.util.AllowCrossOriginPort4200;
import hu.xaddew.lovelyletter.util.DefaultApiErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllowCrossOriginPort4200
@DefaultApiErrorResponses
@RequestMapping("/rules")
@Tag(name = "Rules")
@RequiredArgsConstructor
public class RuleController {

  @Value("${lovely-letter-rules-url}")
  private String rulesUrl;

  @GetMapping("/pdf")
  @Operation(summary = "Szabályfüzet (pdf) lekérdezése")
  @ApiResponse(responseCode = "200", description = "Szabályfüzet pdf formátumban",
      content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
  public ResponseEntity<Object> getRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(rulesUrl)).build();
  }

}
