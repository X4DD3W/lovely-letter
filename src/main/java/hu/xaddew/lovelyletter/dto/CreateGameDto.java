package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játék létrehozása adatmodell")
public class CreateGameDto {

  @Schema(description = "Játékosok nevei")
  private List<String> playerNames;

  @Schema(description = "2019-es változat?")
  private Boolean is2019Version;

  @Schema(description = "Egyedi kártyák nevei")
  private List<String> customCardNames;
}
