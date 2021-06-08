package hu.xaddew.lovelyletter.service;

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
}
