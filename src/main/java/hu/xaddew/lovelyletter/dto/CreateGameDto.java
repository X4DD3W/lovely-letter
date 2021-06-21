package hu.xaddew.lovelyletter.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateGameDto {

  private List<String> nameOfPlayers;
  // TODO add is2019Version (false: original, true: 2019)
  // TODO add customCards (List<String>?) (megadva, mint a nevek, pl. Kili, Bárd stb?)
}
