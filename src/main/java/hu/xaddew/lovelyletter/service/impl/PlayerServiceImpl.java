package hu.xaddew.lovelyletter.service.impl;

import hu.xaddew.lovelyletter.dto.PlayerAndNumberOfLettersDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.enums.ErrorMessage;
import hu.xaddew.lovelyletter.enums.ErrorType;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import hu.xaddew.lovelyletter.service.PlayerService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;

  @Override
  public PlayerKnownInfosDto getAllInfosByPlayerUuidAndRelatedGame(String playerUuid, Game game) {
    Player player = playerRepository.findByUuid(playerUuid);
    PlayerKnownInfosDto knownInfosDto = new PlayerKnownInfosDto();
    if (player != null) {
      knownInfosDto.setMyName(player.getName());
      knownInfosDto.setNumberOfLetters(player.getNumberOfLetters());
      knownInfosDto.setCardsInHand(player.getCardsInHand());
      knownInfosDto.setPlayedCards(player.getPlayedCards());

      if (game != null) {
        knownInfosDto.setGameLogsAboutMe(getGameLogsByPlayerName(player.getName(), game));
        knownInfosDto.setGameHiddenLogsAboutMe(
            getGameHiddenLogsByPlayerName(player.getName(), game));
        knownInfosDto.setAllGameLogs(game.getLog());
        knownInfosDto.setOtherPlayers(
            getOtherPlayersAndNumberOfLettersByPlayerUuidAndGame(playerUuid, game));
      }

    } else {
      throw new GameException(ErrorMessage.PLAYER_NOT_FOUND_ERROR_MESSAGE, ErrorType.NOT_FOUND);
    }
    return knownInfosDto;
  }

  @Override
  public Player findByUuid(String uuid) {
    return playerRepository.findByUuid(uuid);
  }

  public List<String> getGameLogsByPlayerName(String name, Game game) {
    return game.getLog().stream().filter(log -> log.contains(name)).collect(Collectors.toList());
  }

  public List<String> getGameHiddenLogsByPlayerName(String name, Game game) {
    return game.getHiddenLog().stream().filter(log -> log.contains(name))
        .collect(Collectors.toList());
  }

  private List<PlayerAndNumberOfLettersDto> getOtherPlayersAndNumberOfLettersByPlayerUuidAndGame(
      String uuid, Game game) {
    List<PlayerAndNumberOfLettersDto> dtoList = new ArrayList<>();

    game.getPlayersInGame().stream()
        .filter(player -> !player.getUuid().equals(uuid))
        .forEach(player -> {
          PlayerAndNumberOfLettersDto dto = new PlayerAndNumberOfLettersDto();
          dto.setPlayerName(player.getName());
          dto.setNumberOfLetters(player.getNumberOfLetters());
          dtoList.add(dto);
        });

    return dtoList;
  }

}