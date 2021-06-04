package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

  private final GameService gameService;
  private final PlayerRepository playerRepository;

  public Player findByUuid(String uuid) {
    return playerRepository.findByUuid(uuid);
  }

  @Override
  public PlayerKnownInfosDto getAllCardsByPlayerUuid(String playerUuid) {
    Player player = playerRepository.findByUuid(playerUuid);
    PlayerKnownInfosDto knownInfosDto = new PlayerKnownInfosDto();
    if (player != null) {
      knownInfosDto.setNumberOfLetters(player.getNumberOfLetters());
      knownInfosDto.setCardsInHand(player.getCardsInHand());
      knownInfosDto.setPlayedCards(player.getPlayedCards());
      knownInfosDto.setGameLogsAboutMe(
          gameService.findGameLogsByPlayerUuidAndName(playerUuid, player.getName()));
    }
    return knownInfosDto;
  }

}
