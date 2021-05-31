package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayerAllCardsDto;
import hu.xaddew.lovelyletter.model.Player;

public interface PlayerService {

  Player findByUuid(String uuid);

  PlayerAllCardsDto getAllCardsByPlayerUuid(String playerUuid);
}
