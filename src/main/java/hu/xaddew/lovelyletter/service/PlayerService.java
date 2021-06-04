package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.model.Player;

public interface PlayerService {

  Player findByUuid(String uuid);

  PlayerKnownInfosDto getAllCardsByPlayerUuid(String playerUuid);
}
