package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játékos alapadatok adatmodell")
public class PlayerUuidDto {

  @Schema(description = "Játékos neve")
  private String name;

  @Schema(description = "Játékos uuid")
  private String uuid;
}
