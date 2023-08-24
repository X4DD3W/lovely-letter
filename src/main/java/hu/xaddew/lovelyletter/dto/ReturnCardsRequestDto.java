package hu.xaddew.lovelyletter.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnCardsRequestDto {

  private String playerUuid;
  private List<String> cardsToReturn;
}
