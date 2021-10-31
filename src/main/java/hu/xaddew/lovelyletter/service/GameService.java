package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PutBackCardResponseDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsRequestDto;
import hu.xaddew.lovelyletter.model.Game;
import java.time.LocalDateTime;
import java.util.List;

public interface GameService {

  CreatedGameResponseDto createGame(CreateGameDto createGameDto);

  List<GodModeDto> getAllGamesWithSecretInfos();

  List<Game> findAll();

  GameStatusDto getGameStatus(String gameUuid);

  PlayCardResponseDto playCard(PlayCardRequestDto requestDto);

  Game findGameByPlayerUuid(String playerUuid);

  PutBackCardResponseDto putBackCards(PutBackCardsRequestDto requestDto);

  void closeOpenButInactiveGames(LocalDateTime modifyDate);

  void deleteClosedGamesOlderThanAllowed(LocalDateTime modifyDate);
}
