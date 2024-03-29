package hu.xaddew.lovelyletter.dto;

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
public class PlayCardRequestDto {

  private String playerUuid;
  private String cardName;
  private AdditionalInfoDto additionalInfo;

  public PlayCardRequestDto(String playerUuid) {
    this.playerUuid = playerUuid;
  }

  public PlayCardRequestDto(String playerUuid, String cardName) {
    this.playerUuid = playerUuid;
    this.cardName = cardName;
  }
}
