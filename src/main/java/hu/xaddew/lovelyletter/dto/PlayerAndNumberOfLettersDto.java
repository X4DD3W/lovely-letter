package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játékos és leveleinek száma adatmodell")
public class PlayerAndNumberOfLettersDto {

  @Schema(description = "Játékos neve")
  private String playerName;

  @Schema(description = "Levelek száma")
  private Integer numberOfLetters;
}
