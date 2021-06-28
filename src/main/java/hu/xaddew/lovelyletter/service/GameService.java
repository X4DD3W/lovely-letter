package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.ResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsRequestDto;
import hu.xaddew.lovelyletter.model.Game;
import java.util.List;

public interface GameService {

  CreatedGameResponseDto createGame(CreateGameDto createGameDto);

  List<GodModeDto> getAllGamesWithSecretInfos();

  List<Game> findAll();

  GameStatusDto getGameStatus(String gameUuid);

  ResponseDto playCard(PlayCardRequestDto requestDto);

  PlayerKnownInfosDto getAllInfosByPlayerUuid(String playerUuid);

  Game findGameByPlayerUuid(String playerUuid);

  ResponseDto putBackCards(PutBackCardsRequestDto requestDto);
}
