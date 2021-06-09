package hu.xaddew.lovelyletter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerAndNumberOfLettersDto {

  private String playerName;
  private Integer numberOfLetters;
}
