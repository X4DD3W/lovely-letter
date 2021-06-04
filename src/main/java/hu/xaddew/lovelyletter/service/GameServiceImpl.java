package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
    if (isThereAreDuplicatedNamesInGivenPlayerNames(createGameDto)) {
      throw new GameException("Nem szerepelhet két játékos ugyanazzal a névvel!");
    }

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

    List<Integer> orderNumbers = new ArrayList<>();
    for (int i = 1; i <= createGameDto.getNumberOfPlayers(); i++) {
      orderNumbers.add(i);
    }

    playerUuidDtos.forEach(uuidDto -> {
      Player player = new Player();
      player.setUuid(uuidDto.getUuid());
      player.setName(uuidDto.getName());
      player.setGame(game);

      randomIndex = random.nextInt(orderNumbers.size());
      player.setOrderNumber(orderNumbers.get(randomIndex));
      orderNumbers.remove(randomIndex);

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

  private boolean isThereAreDuplicatedNamesInGivenPlayerNames(CreateGameDto createGameDto) {
    return createGameDto.getNameOfPlayers().stream().distinct().count() != createGameDto
        .getNameOfPlayers().size();
  }

  @Override
  public List<GodModeDto> getAllGamesWithSecretInfos() {
    List<Game> games = findAll();
    List<GodModeDto> godModeDtoList = new ArrayList<>();
    games.forEach(game -> {
      List<Card> onlyCardsInDrawDeck = getAvailableCards(game);

      GodModeDto godModeDto = GodModeDto.builder()
          .id(game.getId())
          .uuid(game.getUuid())
          .drawDeck(onlyCardsInDrawDeck)
          .putAsideCard(game.getPutAsideCard())
          .publicCards(game.getPublicCards())
          .playersInGame(game.getPlayersInGame())
          .actualPlayer(game.getActualPlayer())
          .log(game.getLog())
          .isGameOver(game.getIsGameOver())
          .build();
      godModeDtoList.add(godModeDto);
    });

    return godModeDtoList;
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
      statusDto.setPublicCards(game.getPublicCards());
      statusDto.setNumberOfCardsInDrawDeck(getAvailableCards(game).size());
      statusDto.setLog(game.getLog());

      List<PlayerAndPlayedCardsDto> playedCardsByPlayersInGame = new ArrayList<>();
      game.getPlayersInGame().stream()
          .filter(Player::getIsInPlay)
          .forEach(player -> addPlayedCardsToDtoList(player, playedCardsByPlayersInGame));
      statusDto.setPlayedCardsByPlayersInGame(playedCardsByPlayersInGame);

      List<PlayerAndPlayedCardsDto> playedCardsByPlayersOutOfGame = new ArrayList<>();
      game.getPlayersInGame().stream()
          .filter(player -> !player.getIsInPlay())
          .forEach(player -> addPlayedCardsToDtoList(player, playedCardsByPlayersOutOfGame));
      statusDto.setPlayedCardsByPlayersOutOfGame(playedCardsByPlayersOutOfGame);
    }

    return statusDto;
  }

  private void addPlayedCardsToDtoList(Player player, List<PlayerAndPlayedCardsDto> dtoList) {
    PlayerAndPlayedCardsDto dto = new PlayerAndPlayedCardsDto();
    dto.setPlayerName(player.getName());
    dto.setPlayedCards(
        player.getPlayedCards().stream()
            .map(card -> card.getCardValue() + " - " + card.getCardName() + " (" + card.getQuantity() + ")")
            .collect(Collectors.toList()));
    dtoList.add(dto);
  }

  @Override
  public PlayCardResponseDto playCard(PlayCardRequestDto requestDto) {
    PlayCardResponseDto responseDto = new PlayCardResponseDto();
    Player actualPlayer =  playerService.findByUuid(requestDto.getPlayerUuid());
     if (actualPlayer != null) {
       Game game = findGameByPlayerUuid(actualPlayer.getUuid());
       if (game != null) {
         if (actualPlayer.getName().equals(game.getActualPlayer())) {
           if (hasPlayerTheCardSheOrHeWantToPlay(actualPlayer, requestDto.getCardName())) {
             if (requestDto.getCardName().matches("Király|Herceg|Báró|Pap|Őr")) {
               if (requestDto.getAdditionalInfo() != null) {
                 Player targetPlayer = game.getPlayersInGame().stream()
                     .filter(p -> p.getName().equals(requestDto.getAdditionalInfo().getTargetPlayer()))
                     .findFirst().orElse(null);
                 if (targetPlayer != null) {
                   if (!isTargetPlayersLastCardHandmaid(targetPlayer)) {
                     responseDto = processAdditionalInfo(actualPlayer, targetPlayer, game, requestDto);
                     setNextPlayerInOrder(actualPlayer, game);
                     gameRepository.saveAndFlush(game);
                   } else throw new GameException("Az általad választott játékost Szobalány védi.");
                 } else throw new GameException("Nem találtam az általad választott játékost.");
               }
             } else {
               responseDto = processAdditionalInfo(actualPlayer, null, game, requestDto);
               setNextPlayerInOrder(actualPlayer, game);
               gameRepository.saveAndFlush(game);
             }
           } else throw new GameException("Nincsen nálad a kártya, amit ki szeretnél játszani.");
         } else throw new GameException("Nem a te köröd van, " + actualPlayer.getName() + ".");
       } else throw new GameException("Nem találtam játékot ezzel a játékossal.");
     } else throw new GameException("Nem találtam játékost ezzel az uuid-val: " + requestDto.getPlayerUuid());
    return responseDto;
  }

  @Override
  public Game findGameByPlayerUuid(String playerUuid) {
    return gameRepository.findGameByPlayerUuid(playerUuid);
  }

  @Override
  public List<String> findGameLogsByPlayerUuidAndName(String uuid, String name) {
    Game game = findGameByPlayerUuid(uuid);
    return game.getLog().stream().filter(log -> log.contains(name)).collect(Collectors.toList());
  }

  private void setNextPlayerInOrder(Player actualPlayer, Game game) {
    if (isRoundOverBecauseThereIsOnlyOneActivePlayer(game)) {
      game.addLog("A forduló véget ért, mert csak egy játékos maradt bent.");
    } else if (isRoundOverBecauseDrawDeckIsEmptyAndThereAreAtLeastTwoActivePlayer(game)) {
      game.addLog("A forduló véget ért, mert elfogyott a húzópakli.");
    } else {
      List<Player> activePlayers = getActivePlayers(game);

      Player nextActualPlayer;
      if (actualPlayer.getOrderNumber() == game.getPlayersInGame().size()) {
        nextActualPlayer = activePlayers.stream()
            .min(Comparator.comparing(Player::getOrderNumber)).orElse(null);
      } else {
        nextActualPlayer = activePlayers.stream()
            .filter(player -> player.getOrderNumber() > actualPlayer.getOrderNumber())
            .findFirst().orElse(null);
      }

      if (nextActualPlayer != null) {
        game.setActualPlayer(nextActualPlayer.getName());
        game.addLog("Soron lévő játékos: " + game.getActualPlayer());
        drawCard(nextActualPlayer, game);
      }
    }
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
          .is2PlayerPublic(originalCard.getIs2PlayerPublic())
          .isAtAPlayer(originalCard.getIsAtAPlayer())
          .build();
      cardService.save(card);
      newDeck.add(card);
    }
    return newDeck;
  }

  private void putAsideCards(Game game) {
    drawACardToPutAside(game);
    if (game.getPlayersInGame().size() == 2) {
      for (int i = 0; i < 3; i++) {
        drawACardToMakePublicInTwoPlayerMode(game);
      }
    }
  }

  private void drawACardToPutAside(Game game) {
    randomIndex = random.nextInt(getAvailableCards(game).size());
    Card cardToPutAside = getAvailableCards(game).get(randomIndex);
    cardToPutAside.setIsPutAside(true);
  }

  private void drawACardToMakePublicInTwoPlayerMode(Game game) {
    randomIndex = random.nextInt(getAvailableCards(game).size());
    Card cardToMakePublic = getAvailableCards(game).get(randomIndex);
    cardToMakePublic.setIs2PlayerPublic(true);
  }

  private void dealOneCardToAllPlayers(Game game) {
    game.getPlayersInGame().forEach(player -> drawCard(player, game));
  }

  private void determineStartPlayer(Game game) {
    Player firstPlayer = game.getPlayersInGame().stream()
        .filter(player -> player.getOrderNumber() == 1)
        .findFirst().orElse(null);
    if (firstPlayer != null) {
      drawCard(firstPlayer, game);
      game.setActualPlayer(firstPlayer.getName());
    }
  }

  private void drawCard(Player actualPlayer, Game game) {
    List<Card> availableCards = getAvailableCards(game);

    randomIndex = random.nextInt(availableCards.size());
    Card drawnCard = availableCards.get(randomIndex);
    actualPlayer.getCardsInHand().add(drawnCard);
    drawnCard.setIsAtAPlayer(true);
  }

  private void addGameCreationLogs(Game game) {
    game.addLog("Játék létrehozva. Uuid: " + game.getUuid());
    game.addLog("Játékosok: " + game.getPlayersInGame().stream().map(Player::getName).collect(Collectors.joining(", ")));
    game.addLog("Soron lévő játékos: " + game.getActualPlayer());
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
    Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
    String cardNameWantToPlayOut = cardWantToPlayOut.getCardName();
    AdditionalInfoDto info = requestDto.getAdditionalInfo();

    // TODO komoly validáció kell ide!
    //  (határértékek csekkolása, pl. ha mást nem választhatok Herceggel, csak magamat stb...)
    //  Herceg vagy Király mellé Grófnő --> Grófnőt kell eldobni AUTO

    if (cardNameWantToPlayOut.equals("Hercegnő")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      actualPlayer.setIsInPlay(false);
      responseDto.setLastLog(addLogWhenAPlayerMustDiscardPrincess(actualPlayer, game));
    }
    if (cardNameWantToPlayOut.matches("Grófnő|Szobalány")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      responseDto.setLastLog(addLogWhenAPlayerPlaysOutCountessOrHandmaid(actualPlayer, cardNameWantToPlayOut, game));
    }
    if (cardNameWantToPlayOut.equals("Király")) {
      if (info.getTargetPlayer() == null || info.getTargetPlayer().isEmpty()) {
        throw new GameException("Nem választottál másik játékost a kártya hatásához.");
      }
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
    if (cardNameWantToPlayOut.equals("Herceg")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      if (targetPlayer.getName().equals(actualPlayer.getName())) {
        Card cardToDiscard = actualPlayer.getCardsInHand().get(0);
        actualPlayer.getCardsInHand().remove(cardToDiscard);
        actualPlayer.getPlayedCards().add(cardToDiscard);
        if (cardToDiscard.getCardName().equals("Hercegnő")) {
          actualPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(actualPlayer, game));
        } else {
          drawCard(actualPlayer, game);
          responseDto.setLastLog(addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(actualPlayer, cardToDiscard, game));
        }
      } else {
        Card cardToDiscard = targetPlayer.getCardsInHand().get(0);
        targetPlayer.getCardsInHand().remove(cardToDiscard);
        targetPlayer.getPlayedCards().add(cardToDiscard);
        if (cardToDiscard.getCardName().equals("Hercegnő")) {
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, game));
        } else {
          drawCard(targetPlayer, game);
          responseDto.setLastLog(addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, cardToDiscard, game));
        }
      }
    }
    // TODO összehasonlításkor a játékosoknak meg kell tudniuk, mi van a másiknál!
    if (cardNameWantToPlayOut.equals("Báró")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      if (cardValueInHandOf(targetPlayer) < cardValueInHandOf(actualPlayer)) {
        Card cardToDiscard = targetPlayer.getCardsInHand().get(0);
        targetPlayer.getCardsInHand().remove(cardToDiscard);
        targetPlayer.getPlayedCards().add(cardToDiscard);
        targetPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerUseBaronSuccessful(targetPlayer, actualPlayer, game));
      } else if (cardValueInHandOf(targetPlayer) > cardValueInHandOf(actualPlayer)) {
        Card cardToDiscard = actualPlayer.getCardsInHand().get(0);
        actualPlayer.getCardsInHand().remove(cardToDiscard);
        actualPlayer.getPlayedCards().add(cardToDiscard);
        actualPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerUseBaronUnsuccessful(targetPlayer, actualPlayer, game));
      } else {
        responseDto.setLastLog(addLogWhenAPlayerUseBaronAndItsDraw(targetPlayer, actualPlayer, game));
      }
    }
    if (cardNameWantToPlayOut.equals("Pap")) {
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
      responseDto.setMessage(targetPlayer.getName() + " kezében egy " + targetPlayer.getCardsInHand().get(0).getCardName() + " van.");
      responseDto.setLastLog(addLogWhenAPlayerUsePriest(actualPlayer, targetPlayer, game));
    }
    if (cardNameWantToPlayOut.equals("Őr")) {
      if (info.getTargetPlayer() == null || info.getTargetPlayer().isEmpty()) {
        throw new GameException("Nem választottál másik játékost a kártya hatásához.");
      }
      actualPlayer.getCardsInHand().remove(cardWantToPlayOut);
      actualPlayer.getPlayedCards().add(cardWantToPlayOut);
        if (targetPlayer.getCardsInHand().get(0).getCardName().equals(info.getNamedCard())) {
          Card cardToDiscard = targetPlayer.getCardsInHand().get(0);
          targetPlayer.getCardsInHand().remove(cardToDiscard);
          targetPlayer.getPlayedCards().add(cardToDiscard);
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogWhenAPlayerUseGuardSuccessfully(requestDto, actualPlayer, targetPlayer, game));
        } else {
          responseDto.setLastLog(addLogWhenAPlayerUseGuardUnSuccessfully(requestDto, actualPlayer, targetPlayer, game));
        }
    }
    return responseDto;
  }

  private Integer cardValueInHandOf(Player targetPlayer) {
    return targetPlayer.getCardsInHand().get(0).getCardValue();
  }

  private String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerPlaysOutCountessOrHandmaid(Player actualPlayer, String cardNameWantToPlayOut, Game game) {
    return game.addLog(actualPlayer.getName() + " kijátszott egy " + cardNameWantToPlayOut + "t.");
  }

  private String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Királyt használva kártyát cserélt " + targetPlayer.getName() + " játékossal.");
  }

  private String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(Player actualPlayer, Card cardToDiscard, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy " + cardToDiscard.getCardName() + " volt.");
  }

  private String addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName() + " lapját, ami egy Hercegnő volt, így " + targetPlayer.getName() + " kiesett a játékból.");
  }

  private String addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(Player actualPlayer, Player targetPlayer, Card cardToDiscard, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName() + " lapját, ami egy " + cardToDiscard.getCardName() + " volt.");
  }

  private String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította a kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. " + targetPlayer.getGame() + " kiesett a játékból, kézben lévő lapját ("
        + targetPlayer.getCardsInHand().get(0) + ") pedig eldobta.");
  }

  private String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította a kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. " + actualPlayer.getGame() + " kiesett a játékból, kézben lévő lapját ("
        + actualPlayer.getCardsInHand().get(0) + ") pedig eldobta.");
  }

  private String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Báróval összehasonlította a kézben lévő lapját " + targetPlayer.getGame()
        + " kézben lévő lapjával. A lapok értéke azonos volt, így senki sem esett ki a játékból.");
  }

  private String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " megnézte, mi van " + targetPlayer.getName() + " kezében.");
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

  private boolean isRoundOverBecauseThereIsOnlyOneActivePlayer(Game game) {
    boolean isRoundOver = false;
    List<Player> activePlayers = game.getPlayersInGame().stream()
        .filter(Player::getIsInPlay)
        .collect(Collectors.toList());

    if (activePlayers.size() == 1) {
      Player winner = activePlayers.get(0);
      game.addLog(winner.getName() + " nyerte a fordulót!");
      winner.setNumberOfLetters(winner.getNumberOfLetters() + 1);

      giveAdditionalLoveLettersIfOneSpyIsActive(game);

      isRoundOver = true;
      checkLoveLettersAtRoundEnd(game);
    }

    return isRoundOver;
  }

  private boolean isRoundOverBecauseDrawDeckIsEmptyAndThereAreAtLeastTwoActivePlayer(Game game) {
    boolean isRoundOver = false;
    List<Card> availableCards = getAvailableCards(game);

    if (availableCards.isEmpty()) {
      List<Player> activePlayers = getActivePlayers(game);

      Map<Player, Integer> playersAndCardValuesInHand = new HashMap<>();
      for (Player player : activePlayers) {
        playersAndCardValuesInHand.put(player, player.getCardsInHand().get(0).getCardValue());
      }

      List<Player> winners = playersAndCardValuesInHand.entrySet().stream()
          .filter(entry -> entry.getValue() == Collections.max(playersAndCardValuesInHand.values()))
          .map(Entry::getKey)
          .collect(Collectors.toList());

      game.addLog(winners.stream()
          .map(Player::getName)
          .collect(Collectors.joining(" és ")) + "." + " nyerte a fordulót!");

      winners.forEach(winner -> winner.setNumberOfLetters(winner.getNumberOfLetters() + 1));

      giveAdditionalLoveLettersIfOneSpyIsActive(game);

      isRoundOver = true;
      checkLoveLettersAtRoundEnd(game);
    }

    return isRoundOver;
  }

  private void giveAdditionalLoveLettersIfOneSpyIsActive(Game game) {
    // TODO logika
  }

  private void checkLoveLettersAtRoundEnd(Game game) {
    if (isSomeoneHasEnoughLoveLettersToWinTheGame(game)) {
     game.addLog("A játék véget ért, mivel valaki elég szerelmes levelet gyűjtött össze!");
     game.setIsGameOver(true);
    } else {
      resetPlayers(game.getPlayersInGame());
      resetGame(game);
      game.addLog("Új forduló kezdődik. Soron lévő játékos: " + game.getActualPlayer());
    }
    gameRepository.save(game);
  }

  private void resetPlayers(List<Player> playersInGame) {
    List<Integer> orderNumbers = new ArrayList<>();
    for (int i = 1; i <= playersInGame.size(); i++) {
      orderNumbers.add(i);
    }

    playersInGame.forEach(player -> {
      player.getCardsInHand().clear();
      player.getPlayedCards().clear();
      player.setIsInPlay(true);

      randomIndex = random.nextInt(orderNumbers.size());
      player.setOrderNumber(orderNumbers.get(randomIndex));
      orderNumbers.remove(randomIndex);
    });
  }

  private void resetGame(Game game) {
    game.getDrawDeck().forEach(card -> {
      card.setIsPutAside(false);
      card.setIs2PlayerPublic(false);
      card.setIsAtAPlayer(false);
    });
    putAsideCards(game);
    dealOneCardToAllPlayers(game);
    determineStartPlayer(game);
  }

  private boolean isSomeoneHasEnoughLoveLettersToWinTheGame(Game game) {
    boolean isGameOver = false;
    int requiredLetters;
    if (game.getPlayersInGame().size() == 2) {
      requiredLetters = 7;
    } else if (game.getPlayersInGame().size() == 3) {
      requiredLetters = 5;
    } else {
      requiredLetters = 4;
    }

    List<Player> winners = new ArrayList<>();
    for (Player player : game.getPlayersInGame()) {
      if (player.getNumberOfLetters() >= requiredLetters) {
        winners.add(player);
      }
    }

    if (!winners.isEmpty()) {
      game.addLog("A játék véget ért! Gratuláunk, " + winners.stream()
          .map(Player::getName)
          .collect(Collectors.joining(" és ")) + "!");
      isGameOver = true;
    }
    return isGameOver;
  }

  private List<Player> getActivePlayers(Game game) {
    return game.getPlayersInGame().stream()
        .filter(Player::getIsInPlay)
        .collect(Collectors.toList());
  }

  private List<Card> getAvailableCards(Game game) {
    return game.getDrawDeck().stream()
        .filter(card -> !card.getIsPutAside())
        .filter(card -> !card.getIs2PlayerPublic())
        .filter(card -> !card.getIsAtAPlayer())
        .collect(Collectors.toList());
  }
}
