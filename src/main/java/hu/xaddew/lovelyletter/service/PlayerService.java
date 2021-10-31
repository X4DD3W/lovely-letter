package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;

public interface PlayerService {

  PlayerKnownInfosDto getAllInfosByPlayerUuidAndRelatedGame(String playerUuid, Game game);

  Player findByUuid(String uuid);
}
