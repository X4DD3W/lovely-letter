package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kártyakijátszáshoz szükséges további információk adatmodell")
public class AdditionalInfoDto {

  @Schema(description = "Célpont játékos neve")
  private String targetPlayer;

  @Schema(description = "Megnevezett kártya")
  private String namedCard;
}
