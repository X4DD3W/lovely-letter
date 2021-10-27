package hu.xaddew.lovelyletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kártya kijátszása adatmodell")
public class PlayCardRequestDto {

  @Schema(description = "Játékos uuid")
  private String playerUuid;

  @Schema(description = "Kijátszani kívánt kártya neve")
  private String cardName;

  @Schema(description = "Kártyakijátszáshoz szükséges további információk")
  private AdditionalInfoDto additionalInfo;

  public PlayCardRequestDto(String playerUuid) {
    this.playerUuid = playerUuid;
  }

  public PlayCardRequestDto(String playerUuid, String cardName) {
    this.playerUuid = playerUuid;
    this.cardName = cardName;
  }
}
