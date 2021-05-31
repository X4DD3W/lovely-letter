package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardResponseDto {

  private String cardName;
  private Integer cardValue;
  private Integer quantity;
  private String description;
}
