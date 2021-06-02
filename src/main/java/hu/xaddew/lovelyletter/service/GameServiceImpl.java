package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerAndPlayedCardsDto;
import hu.xaddew.lovelyletter.dto.PlayerUuidDto;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.util.ArrayList;
import java.util.LinkedList;
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
  private final OriginalCardService originalCardService;
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
      player.setGame(game);
      players.add(player);
    });

    game.setPlayersInGame(players);
    initDeckAndPutAsideCards(game);
    dealOneCardToAllPlayers(game);
    determineStartPlayer(game);

    addGameCreationLogs(game);
    gameRepository.save(game);
    playerRepository.saveAll(players);

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
      statusDto.setNumberOfCardsInDrawDeck((int) game.getDrawDeck().stream()
          .filter(card -> !card.getIsPutAside())
          .count());
      statusDto.setLog(game.getLog());

      List<PlayerAndPlayedCardsDto> playedCardsByPlayersInGame = new ArrayList<>();
      game.getPlayersInGame().stream()
          .filter(Player::getIsInPlay)
          .forEach(player -> {
        PlayerAndPlayedCardsDto dto = new PlayerAndPlayedCardsDto();
        dto.setPlayerName(player.getName());
        dto.setPlayedCards(
            player.getPlayedCards().stream().map(Card::getCardName).collect(Collectors.toList()));
        playedCardsByPlayersInGame.add(dto);
      });
      statusDto.setPlayedCardsByPlayersInGame(playedCardsByPlayersInGame);

      List<PlayerAndPlayedCardsDto> playedCardsByPlayersOutOfGame = new ArrayList<>();
      game.getPlayersInGame().stream()
          .filter(player -> !player.getIsInPlay())
          .forEach(player -> {
        PlayerAndPlayedCardsDto dto = new PlayerAndPlayedCardsDto();
        dto.setPlayerName(player.getName());
        dto.setPlayedCards(
            player.getPlayedCards().stream().map(Card::getCardName).collect(Collectors.toList()));
        playedCardsByPlayersOutOfGame.add(dto);
      });
      statusDto.setPlayedCardsByPlayersOutOfGame(playedCardsByPlayersOutOfGame);
    }

    return statusDto;
  }

  @Override
  public PlayCardResponseDto playCard(PlayCardRequestDto requestDto) {
    // TODO komoly validáció kell ide :)
    PlayCardResponseDto responseDto = new PlayCardResponseDto();
    Player actualPlayer =  playerService.findByUuid(requestDto.getPlayerUuid());
     if (actualPlayer != null) {
       Game game = findGameByPlayerUuid(actualPlayer.getUuid());
       if (game != null) {
         if (actualPlayer.getName().equals(game.getActualPlayer())) {
           if (hasPlayerTheCardSheOrHeWantToPlay(actualPlayer, requestDto.getCardName())) {
             // TODO targetPlayer-t nem mindig kell csekkolni!
             if (requestDto.getAdditionalInfo() != null) {
               Player targetPlayer = game.getPlayersInGame().stream()
                   .filter(p -> p.getName().equals(requestDto.getAdditionalInfo().getTargetPlayer()))
                   .findFirst().orElse(null);
               if (targetPlayer != null) {
                 if (!isTargetPlayersLastCardHandmaid(targetPlayer)) {
                   // TODO bővíteni ezt a logikát
                   responseDto = processAdditionalInfo(actualPlayer, targetPlayer, game, requestDto);
                   // TODO következő játékos jön
                 } else throw new GameException("Targeted player is protected by a Handmaid.");
               } else throw new GameException("Targeted player is not exists.");
             }
           } else throw new GameException("You doesn't have the card you want to play.");
         } else throw new GameException("It's not your turn, " + actualPlayer.getName() + ".");
       } else throw new GameException("Game not found.");
     } else throw new GameException("Player not found with uuid " + requestDto.getPlayerUuid());
    return responseDto;
  }

  @Override
  public Game findGameByPlayerUuid(String playerUuid) {
    return gameRepository.findGameByPlayerUuid(playerUuid);
  }

  private void initDeckAndPutAsideCards(Game game) {
    List<OriginalCard> originalCards = originalCardService.findAll();
    List<Card> newDeckFromOriginal = createNewDrawDeck(originalCards);
    game.setDrawDeck(newDeckFromOriginal);
    newDeckFromOriginal.forEach(card -> card.setGame(game));
    putAsideCards(game);
  }

  private List<Card> createNewDrawDeck(List<OriginalCard> originalCards) {
    List<Card> newDeck = new LinkedList<>();
    for (OriginalCard originalCard : originalCards) {
      Card card = Card.builder()
          .cardName(originalCard.getCardName())
          .cardValue(originalCard.getCardValue())
          .quantity(originalCard.getQuantity())
          .description(originalCard.getDescription())
          .isPutAside(originalCard.getIsPutAside())
          .build();
      cardService.save(card);
      newDeck.add(card);
    }
    return newDeck;
  }

  private void putAsideCards(Game game) {
    if (game.getPlayersInGame().size() == 2) {
      for (int i = 0; i < 3; i++) {
        drawACardToPutAside(game);
      }
    } else {
      drawACardToPutAside(game);
    }
  }

  private void drawACardToPutAside(Game game) {
    randomIndex = random.nextInt(game.getDrawDeck().size());
    game.getDrawDeck().get(randomIndex).setIsPutAside(true);
    game.getDrawDeck().remove(game.getDrawDeck().get(randomIndex));
  }

  private void dealOneCardToAllPlayers(Game game) {
    game.getPlayersInGame().forEach(player -> {
      randomIndex = random.nextInt(game.getDrawDeck().size());
      player.getCardsInHand().add(game.getDrawDeck().get(randomIndex));
      game.getDrawDeck().remove(game.getDrawDeck().get(randomIndex));
    });
  }

  private void determineStartPlayer(Game game) {
    randomIndex = random.nextInt(game.getPlayersInGame().size());
    Player actualPlayer = game.getPlayersInGame().get(randomIndex);
    drawACard(actualPlayer, game);
    game.setActualPlayer(actualPlayer.getName());
  }

  private void drawACard(Player actualPlayer, Game game) {
    randomIndex = random.nextInt(game.getDrawDeck().size());
    Card drawnCard = game.getDrawDeck().get(randomIndex);
    game.getDrawDeck().remove(drawnCard);
    actualPlayer.getCardsInHand().add(drawnCard);
  }

  private void addGameCreationLogs(Game game) {
    game.addLog("Game is created with uuid " + game.getUuid());
    game.addLog("Players are: " + game.getPlayersInGame().stream().map(Player::getName).collect(Collectors.joining(", ")));
    game.addLog("First player is " + game.getActualPlayer());
  }

  private boolean hasPlayerTheCardSheOrHeWantToPlay(Player player, String cardName) {
    return player.getCardsInHand().stream()
        .map(Card::getCardName)
        .collect(Collectors.toList())
        .contains(cardName);
  }

  private boolean isTargetPlayersLastCardHandmaid(Player targetPlayer) {
    if (targetPlayer.getPlayedCards().isEmpty()) {
      return false;
    } else {
      int lastIndex = targetPlayer.getPlayedCards().size() - 1;
      return targetPlayer.getPlayedCards().get(lastIndex).getCardName().equals("Szobalány");
    }
  }

  private PlayCardResponseDto processAdditionalInfo(Player actualPlayer, Player targetPlayer,
      Game game, PlayCardRequestDto requestDto) {
    PlayCardResponseDto responseDto = new PlayCardResponseDto();
    Card cardWantToPlayOut = cardService.findCardByCardName(requestDto.getCardName());
    String cardNameWantToPlayOut = cardWantToPlayOut.getCardName();

    // TODO komoly validáció kell ide!
    //  (határértékek csekkolása, pl. ha mást nem választhatok Herceggel, csak magamat stb...)

    // TODO minden kártyakijátszáskor logoljunk és akkor az utolsó log (is) legyen visszaadva

    if (cardNameWantToPlayOut.equals("Hercegnő")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      actualPlayer.setIsInPlay(false);
      responseDto.setLastLog(addLogWhenAPlayerMustDiscardPrincess(actualPlayer, game));
      checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
    }
    // TODO "Kém" egyelőre kivéve a játékból.
    if (cardNameWantToPlayOut.matches("Grófnő|Szobalány|Kém")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      responseDto.setLastLog(addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpy(actualPlayer, cardNameWantToPlayOut, game));
    }
    if (cardNameWantToPlayOut.equals("Király")) {
      // TODO nullCheck itt és Őrnél is, ha a targetPlayer üres, errorResponse
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      Card actualPlayersCardInHand = actualPlayer.getCardsInHand().get(0);
      Card targetPlayersCardInHand = targetPlayer.getCardsInHand().get(0);
      actualPlayer.getCardsInHand().remove(actualPlayersCardInHand);
      actualPlayer.getCardsInHand().add(targetPlayersCardInHand);
      targetPlayer.getCardsInHand().remove(targetPlayersCardInHand);
      targetPlayer.getCardsInHand().add(actualPlayersCardInHand);
      responseDto.setLastLog(addLogWhenAPlayerUseKing(actualPlayer, targetPlayer, game));
    }
    // TODO linkedList a sorrend miatt? :/ "Kancellár" egyelőre kivéve a játékból.
    if (cardNameWantToPlayOut.equals("Kancellár")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
    }
    if (cardNameWantToPlayOut.equals("Herceg")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      if (targetPlayer.getName().equals(actualPlayer.getName())) {
        Card cardToDiscard = actualPlayer.getCardsInHand().get(0);
        // TODO check, hogy ez a Hercegnő volt-e!
        actualPlayer.getCardsInHand().remove(cardToDiscard);
        actualPlayer.getPlayedCards().add(cardToDiscard);
        drawACard(actualPlayer, game);
        // TODO log
      } else {
        Card cardToDiscard = targetPlayer.getCardsInHand().get(0);
        // TODO check, hogy ez a Hercegnő volt-e!
        targetPlayer.getCardsInHand().remove(cardToDiscard);
        targetPlayer.getPlayedCards().add(cardToDiscard);
        drawACard(targetPlayer, game);
        // TODO log
      }
    }
    if (cardNameWantToPlayOut.equals("Báró")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      if (cardValueInHandOf(targetPlayer) < cardValueInHandOf(actualPlayer)) {
        // TODO ha kiesik vki, el kell dobnia a lapját!
        targetPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerUseBaronSuccessful(targetPlayer, actualPlayer, game));
        checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
      } else if (cardValueInHandOf(targetPlayer) > cardValueInHandOf(actualPlayer)) {
        // TODO ha kiesik vki, el kell dobnia a lapját!
        actualPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerUseBaronUnsuccessful(targetPlayer, actualPlayer, game));
        checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
      } else {
        responseDto.setLastLog(addLogWhenAPlayerUseBaronAndItsDraw(targetPlayer, actualPlayer, game));
      }
    }
    if (cardNameWantToPlayOut.equals("Pap")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      responseDto.setMessage(targetPlayer + " kezében egy " + targetPlayer.getCardsInHand().get(0).getCardName() + " van.");
      responseDto.setLastLog(addLogWhenAPlayerUsePriest(actualPlayer, targetPlayer, game));
    }
    if (cardNameWantToPlayOut.equals("Őr")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      AdditionalInfoDto info = requestDto.getAdditionalInfo();
        if (targetPlayer.getCardsInHand().get(0).getCardName().equals(info.getNamedCard())) {
          // TODO ha kiesik vki, el kell dobnia a lapját!
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogWhenAPlayerUseGuardSuccessfully(requestDto, actualPlayer, targetPlayer, game));
          checkPlayerNumbersIfSomeoneIsOutOfTheGame(game);
        } else {
          responseDto.setLastLog(addLogWhenAPlayerUseGuardUnSuccessfully(requestDto, actualPlayer, targetPlayer, game));
        }
    }
    gameRepository.saveAndFlush(game);
    return responseDto;
  }

  private Integer cardValueInHandOf(Player targetPlayer) {
    return targetPlayer.getCardsInHand().get(0).getCardValue();
  }

  private String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpy(Player actualPlayer, String cardNameWantToPlayOut, Game game) {
    return game.addLog(actualPlayer.getName() + " kijátszott egy " + cardNameWantToPlayOut + "t.");
  }

  private String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Királyt használva kártyát cserélt " + targetPlayer.getName() + " játékossal.");
  }

  private String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. " + targetPlayer.getGame() + " kiesett a játékból, kézben lévő lapját ("
        + targetPlayer.getCardsInHand().get(0) + ") eldobta.");
  }

  private String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. " + actualPlayer.getGame() + " kiesett a játékból, kézben lévő lapját ("
        + actualPlayer.getCardsInHand().get(0) + ") eldobta.");
  }

  private String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. A lapok értéke azonos volt, így senki sem esett ki a játékból.");
  }

  private String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " megnézte, mi van " + targetPlayer.getGame() + " kezében.");
  }

  private String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto, Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName() + " kezében " + namedCard + " van. Így igaz. "
        + targetPlayer.getName() + " kiesett a játékból.");
  }

  private String addLogWhenAPlayerUseGuardUnSuccessfully(PlayCardRequestDto requestDto, Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName() + " kezében " + namedCard + " van. Nem talált.");
  }

  private void checkPlayerNumbersIfSomeoneIsOutOfTheGame(Game game) {
    if (game.getPlayersInGame().size() == 1) {
      game.addLog(game.getPlayersInGame().get(0) + " wins the round!");
      // TODO resetelni a játékot (visszaállítani mindent game/create utáni állapotba,
      //    KIVÉVE játékosok szerelmesleveleit!
    }
  }
}
