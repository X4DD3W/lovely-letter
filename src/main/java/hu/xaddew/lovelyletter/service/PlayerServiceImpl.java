package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayerAllCardsDto;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;

  public Player findByUuid(String uuid) {
    return playerRepository.findByUuid(uuid);
  }

  @Override
  public PlayerAllCardsDto getAllCardsByPlayerUuid(String playerUuid) {
    Player player = playerRepository.findByUuid(playerUuid);
    PlayerAllCardsDto allCardsDto = new PlayerAllCardsDto();
    if (player != null) {
      allCardsDto.setCardsInHand(player.getCardsInHand());
      allCardsDto.setPlayedCards(player.getPlayedCards());
    }
    return allCardsDto;
  }

}
