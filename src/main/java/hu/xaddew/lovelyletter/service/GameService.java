package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import java.util.List;

public interface GameService {

  CreatedGameResponseDto createGame(CreateGameDto createGameDto);

  List<Game> findAll();

  GameStatusDto getGameStatus(String gameUuid);

  String playCard(PlayCardRequestDto requestDto);

  Game findGameByPlayerUuid(String playerUuid);
}
