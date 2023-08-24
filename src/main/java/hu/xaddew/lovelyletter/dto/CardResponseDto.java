package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardResponseDto {

  private String name;
  private String nameEnglish;
  private Integer value;
  private Integer quantity;
  private String description;
}
