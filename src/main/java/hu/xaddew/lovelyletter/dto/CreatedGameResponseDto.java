package hu.xaddew.lovelyletter.dto;

import java.util.List;
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
public class CreatedGameResponseDto {

  private String gameUuid;
  private List<PlayerUuidDto> playerUuidDtos;
}
