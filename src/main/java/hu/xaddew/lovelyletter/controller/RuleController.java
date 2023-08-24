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

  @Value("${lovely-letter-hu-rules-url}")
  private String huRulesUrl;

  @Value("${lovely-letter-en-rules-url}")
  private String enRulesUrl;

  @GetMapping("/pdf/hu")
  @Operation(summary = "Get hungarian rulebook (pdf)")
  @ApiResponse(responseCode = "200", description = "Hungarian rulebook in PDF format",
      content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
  public ResponseEntity<Object> getHungarianRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(huRulesUrl)).build();
  }

  @GetMapping("/pdf/en")
  @Operation(summary = "Get english rulebook (pdf)")
  @ApiResponse(responseCode = "200", description = "English rulebook in PDF format",
          content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
  public ResponseEntity<Object> getEnglishRules() {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(enRulesUrl)).build();
  }

}
