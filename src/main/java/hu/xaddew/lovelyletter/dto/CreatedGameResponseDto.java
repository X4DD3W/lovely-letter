package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Játék létrehozása válasz adatmodell")
public class CreatedGameResponseDto {

  @Schema(description = "Játék uuid")
  private String gameUuid;

  @Schema(description = "Játékosok alapdatainak listája")
  private List<PlayerUuidDto> playerUuidDtos;
}
