package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayCardRequestDto {

  private String playerUuid;
  private String cardName;
  private AdditionalInfoDto additionalInfo;
}
