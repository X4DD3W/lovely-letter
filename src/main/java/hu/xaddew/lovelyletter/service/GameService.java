package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.CreateGameRequestDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.ReturnCardResponseDto;
import hu.xaddew.lovelyletter.dto.ReturnCardsRequestDto;
import hu.xaddew.lovelyletter.model.Game;
import java.time.LocalDateTime;
import java.util.List;

public interface GameService {

  CreatedGameResponseDto createGame(CreateGameRequestDto createGameDto);

  List<GodModeDto> getAllGamesWithSecretInfos();

  List<Game> findAll();

  GameStatusDto getGameStatus(String gameUuid);

  PlayCardResponseDto playCard(PlayCardRequestDto requestDto);

  Game findGameByPlayerUuid(String playerUuid);

  ReturnCardResponseDto returnCardsToDrawDeck(ReturnCardsRequestDto requestDto);

  void closeOpenButInactiveGames(LocalDateTime modifyDate);

  void deleteClosedGamesOlderThanAllowed(LocalDateTime modifyDate);
}
