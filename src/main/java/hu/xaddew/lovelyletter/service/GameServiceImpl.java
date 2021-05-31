package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayerAndPlayedCardsDto;
import hu.xaddew.lovelyletter.dto.PlayerUuidDto;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

  private int randomIndex;

  private final Random random;
  private final CardService cardService;
  private final PlayerService playerService;
  private final GameRepository gameRepository;
  private final PlayerRepository playerRepository;

  @Override
  public CreatedGameResponseDto createGame(CreateGameDto createGameDto) {
    CreatedGameResponseDto responseDto = new CreatedGameResponseDto();
    Game game = new Game();
    List<PlayerUuidDto> playerUuidDtos = new ArrayList<>();
    List<Player> players = new ArrayList<>();

    game.setUuid(UUID.randomUUID().toString());

    createGameDto.getNameOfPlayers().forEach(name -> {
      PlayerUuidDto dto = new PlayerUuidDto();
      dto.setName(name);
      dto.setUuid(UUID.randomUUID().toString());
      playerUuidDtos.add(dto);
    });

    playerUuidDtos.forEach(uuidDto -> {
      Player player = new Player();
      player.setUuid(uuidDto.getUuid());
      player.setName(uuidDto.getName());
      playerRepository.save(player);
      players.add(player);
    });

    game.setPlayersInGame(players);
    initDeckAndPutAsideCards(game);
    dealOneCardToAllPlayers(game);
    determineStartPlayer(game);

    addGameCreationLogs(game);
    gameRepository.save(game);

    responseDto.setGameUuid(game.getUuid());
    responseDto.setPlayerUuidDtos(playerUuidDtos);
    return responseDto;
  }

  @Override
  public List<Game> findAll() {
    return gameRepository.findAll();
  }

  @Override
  public GameStatusDto getGameStatus(String gameUuid) {
    GameStatusDto statusDto = new GameStatusDto();
    Game game = gameRepository.findByUuid(gameUuid);

    if (game != null) {
      statusDto.setActualPlayer(game.getActualPlayer());
      statusDto.setNumberOfCardsInDrawDeck(game.getDrawDeck().size());
      statusDto.setLog(game.getLog());

      List<PlayerAndPlayedCardsDto> playedCardsByPlayers = new ArrayList<>();

      game.getPlayersInGame().forEach(player -> {
        PlayerAndPlayedCardsDto dto = new PlayerAndPlayedCardsDto();
        dto.setPlayerName(player.getName());
        dto.setPlayedCards(
            player.getPlayedCards().stream().map(Card::getCardName).collect(Collectors.toList()));
        playedCardsByPlayers.add(dto);
      });
      game.getPlayersOutOfGame().forEach(player -> {
        PlayerAndPlayedCardsDto dto = new PlayerAndPlayedCardsDto();
        dto.setPlayerName(player.getName());
        dto.setPlayedCards(
            player.getPlayedCards().stream().map(Card::getCardName).collect(Collectors.toList()));
        playedCardsByPlayers.add(dto);
      });
      statusDto.setPlayedCardsByPlayers(playedCardsByPlayers);
    }

    return statusDto;
  }

  @Override
  public String playCard(PlayCardRequestDto requestDto) {
    // TODO komoly validáció kell ide :)
    Player actualPlayer =  playerService.findByUuid(requestDto.getPlayerUuid());
     if (actualPlayer != null) {
       Game game = findGameByPlayerUuid(actualPlayer.getUuid());
       if (game != null) {
         if (actualPlayer.getName().equals(game.getActualPlayer())) {
           if (hasPlayerTheCardSheOrHeWantToPlay(actualPlayer, requestDto.getCardName())) {
             // TODO bővíteni ezt a logikát
             processAdditionalInfo(actualPlayer, game, requestDto);
           } else throw new GameException("You doesn't have the card you want to play.");
         } else throw new GameException("It's not your turn, " + actualPlayer.getName() + ".");
       } else throw new GameException("Game not found.");
     } else throw new GameException("Player not found with uuid " + requestDto.getPlayerUuid());
    return null;
  }

  @Override
  public Game findGameByPlayerUuid(String playerUuid) {
    return gameRepository.findGameByPlayerUuid(playerUuid);
  }

  private void initDeckAndPutAsideCards(Game game) {
    List<Card> originalDeck = cardService.findAll();
    game.setDrawDeck(originalDeck);
    game.setPutAsideCards(putAsideCards(game));
  }

  private List<Card> putAsideCards(Game game) {
    if (game.getPlayersInGame().size() == 2) {
      for (int i = 0; i < 3; i++) {
        drawACardToPutAside(game);
      }
    } else {
      drawACardToPutAside(game);
    }
    return game.getPutAsideCards();
  }

  private void drawACardToPutAside(Game game) {
    randomIndex = random.nextInt(game.getDrawDeck().size());
    game.getPutAsideCards().add(game.getDrawDeck().get(randomIndex));
    game.getDrawDeck().remove(game.getDrawDeck().get(randomIndex));
  }

  private void dealOneCardToAllPlayers(Game game) {
    game.getPlayersInGame().forEach(player -> {
      randomIndex = random.nextInt(game.getDrawDeck().size());
      player.getCardsInHand().add(game.getDrawDeck().get(randomIndex));
      game.getDrawDeck().remove(game.getDrawDeck().get(randomIndex));
      playerRepository.saveAndFlush(player);
    });
  }

  private void determineStartPlayer(Game newGame) {
    randomIndex = random.nextInt(newGame.getPlayersInGame().size());
    newGame.setActualPlayer(newGame.getPlayersInGame().get(randomIndex).getName());
  }

  private void addGameCreationLogs(Game game) {
    game.addLog("Game is created with uuid " + game.getUuid());
    game.addLog("Players are: " + game.getPlayersInGame().stream().map(Player::getName)
        .collect(Collectors.joining(", ")));
    game.addLog("First player is " + game.getActualPlayer());
  }

  private boolean hasPlayerTheCardSheOrHeWantToPlay(Player player, String cardName) {
    return player.getCardsInHand().stream()
        .map(Card::getCardName)
        .collect(Collectors.toList())
        .contains(cardName);
  }

  private void processAdditionalInfo(Player actualPlayer, Game game, PlayCardRequestDto requestDto) {
    Card cardWantToPlay = cardService.findCardByCardName(requestDto.getCardName());
    String cardNameWantToPlay = cardWantToPlay.getCardName();
    // TODO valamit minden esetben vissza kell adni. Azt majd itt hozzuk létre.


    if (cardNameWantToPlay.equals("Hercegnő")) {
      game.getPlayersInGame().remove(actualPlayer);
      game.getPlayersOutOfGame().add(actualPlayer);
      addLogWhenAPlayerMustDiscardPrincess(actualPlayer, game);
      checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
    }
    if (cardNameWantToPlay.matches("Grófnő|Szobalány|Kém")) {
      actualPlayer.getPlayedCards().add(cardWantToPlay);
      actualPlayer.getCardsInHand().remove(cardWantToPlay);
      // addLog
      // TODO saveAndFlush player és game? vagy elég az utóbbi?
      gameRepository.saveAndFlush(game);
    }
    if (cardNameWantToPlay.equals("Király")) {
      // kit választott
      // játékosok kézben lévő lapjai cserélődnek
    }
    if (cardNameWantToPlay.equals("Kancellár")) {
      // izgi lesz...
    }
    if (cardNameWantToPlay.equals("Herceg")) {
      // kit választott
      // a választott lapja eldobódik és kap egy másikat
    }
    if (cardNameWantToPlay.equals("Báró")) {
      // kit választott
    }
    if (cardNameWantToPlay.equals("Pap")) {
      // kit választott
    }
    if (cardNameWantToPlay.equals("Őr")) {
      AdditionalInfoDto info = requestDto.getAdditionalInfo();
      Player targetPlayer = game.getPlayersInGame().stream()
          .filter(p -> p.getName().equals(info.getTargetPlayer()))
          .findFirst().orElse(null);
      if (targetPlayer != null) {
        if (targetPlayer.getCardsInHand().get(0).getCardName().equals(info.getNamedCard())) {
          game.getPlayersInGame().remove(targetPlayer);
          game.getPlayersOutOfGame().add(targetPlayer);
          addLogWhenAPlayerUseGuardSuccessfully(requestDto, targetPlayer, game);
          checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
        } else {
          addLogWhenAPlayerUseGuardUnSuccessfully(requestDto, targetPlayer, game);
        }
      } else throw new GameException("Targeted player is not exists or out of the game.");
    }
  }

  // TODO kideríteni, mikor és mit kell menteni? Player is pl?
  private void addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
    gameRepository.saveAndFlush(game);
  }

  private void addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto, Player targetPlayer, Game game) {
    Player actualPlayer = playerService.findByUuid(requestDto.getPlayerUuid());
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    game.addLog(actualPlayer + " Őrrel " + namedCard + " tippelt " + targetPlayer.getName() + " és eltalálta! "
        + targetPlayer.getName() + " kiesett.");
    gameRepository.saveAndFlush(game);
  }

  private void addLogWhenAPlayerUseGuardUnSuccessfully(PlayCardRequestDto requestDto, Player targetPlayer, Game game) {
    Player actualPlayer = playerService.findByUuid(requestDto.getPlayerUuid());
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    game.addLog(actualPlayer + " Őrrel " + namedCard + " tippelt " + targetPlayer.getName() + ", de nem talált.");
    gameRepository.saveAndFlush(game);
  }

  private void checkPlayerNumbersIfSomeoneIsOutOfTheGame(Game game) {
    if (game.getPlayersInGame().size() == 1) {
      game.addLog(game.getPlayersInGame().get(0) + " wins the round!");
      // TODO resetelni a játékot (visszaállítani mindent game/create utáni állapotba,
      //    KIVÉVE játékosok szerelmesleveleit!
    }
  }
}
