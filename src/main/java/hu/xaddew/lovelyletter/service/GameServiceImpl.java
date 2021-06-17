package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayCardResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerAndNumberOfLettersDto;
import hu.xaddew.lovelyletter.dto.PlayerAndPlayedCardsDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
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

  private static final String MISSING_REQUEST_DTO_ERROR_MESSAGE = "Játékosok nevének megadása szükséges.";
  private static final String PLAYER_NUMBER_ERROR_MESSAGE = "A játékosok száma 2, 3 vagy 4 lehet.";
  private static final String PLAYER_NAME_ERROR_MESSAGE = "Nem szerepelhet két játékos ugyanazzal a névvel!";
  private static final String PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE = "Az általad választott játékost Szobalány védi.";
  private static final String PLAYER_SELF_TARGETING_ERROR_MESSAGE = "Király, Báró, Pap és Őr kijátszásakor nem választhatod saját magadat.";
  private static final String PLAYER_NOT_FOUND_ERROR_MESSAGE = "Nem találtam az általad választott játékost.";
  private static final String PLAYER_NOT_SELECTED_ERROR_MESSAGE = "Nem választottál másik játékost a kártya hatásához.";
  private static final String HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE = "Nincsen nálad a kártya, amit ki szeretnél játszani.";
  private static final String COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE = "Ha a Grófnő a Királlyal vagy a Herceggel egyszerre van a kezedben, a Grófnőt kell eldobnod.";
  private static final String NOT_YOUR_TURN_ERROR_MESSAGE = "Nem a te köröd van, ";
  private static final String NO_GAME_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE = "Nem találtam játékot ezzel az uuid-val: ";
  private static final String NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE = "Nem találtam játékot ezzel a játékossal.";
  private static final String NO_PLAYER_FOUND_WITH_GIVEN_UUID = "Nem találtam játékost ezzel az uuid-val: ";
  private static final String ACTUAL_PLAYER_IS = "Soron lévő játékos: ";
  private static final String GAME_IS_CREATED_UUID = "Játék létrehozva. Uuid: ";
  private static final String PLAYERS_ARE = "Játékosok: ";
  private static final String ROUND_IS_OVER_ONLY_ONE_PLAYER_LEFT = "A forduló véget ért, mert csak egy játékos maradt bent.";
  private static final String ROUND_IS_OVER_DRAW_DECK_IS_EMPTY = "A forduló véget ért, mert elfogyott a húzópakli.";
  private static final String WON_THE_ROUND = " nyerte a fordulót!";
  private static final String GAME_IS_OVER_STATUS_MESSAGE = "A játék véget ért, mivel valaki elég szerelmes levelet gyűjtött össze!";
  private static final String NEW_ROUND_BEGINS_STATUS_MESSAGE = "Új forduló kezdődik. ";
  private static final String CONGRATULATE = "Gratulálunk, ";
  private static final String PRINCESS = "Hercegnő";
  private static final String COUNTESS = "Grófnő";
  private static final String KING = "Király";
  private static final String PRINCE = "Herceg";
  private static final String HANDMAID = "Szobalány";
  private static final String BARON = "Báró";
  private static final String PRIEST = "Pap";
  private static final String GUARD = "Őr";
  private static final String COMPARE_CARD_IN_HAND_WITH = " Báróval összehasonlította a kézben lévő lapját ";
  private static final String KING_OR_PRINCE_OR_BARON_OR_PRIEST_OR_GUARD_REGEX = "Király|Herceg|Báró|Pap|Őr";
  private static final String KING_OR_BARON_OR_PRIEST_OR_GUARD_REGEX = "Király|Báró|Pap|Őr";

  private int randomIndex;

  private final Random random;
  private final CardService cardService;
  private final OriginalCardService originalCardService;
  private final PlayerService playerService;
  private final GameRepository gameRepository;
  private final PlayerRepository playerRepository;

  @Override
  public CreatedGameResponseDto createGame(CreateGameDto createGameDto) {
    if (createGameDto == null) {
      throw new GameException(MISSING_REQUEST_DTO_ERROR_MESSAGE);
    }

    if (isGivenNumberOfPlayersOutOfAllowedRange(createGameDto)) {
      throw new GameException(PLAYER_NUMBER_ERROR_MESSAGE);
    }

    if (isThereAreDuplicatedNamesInGivenPlayerNames(createGameDto)) {
      throw new GameException(PLAYER_NAME_ERROR_MESSAGE);
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
    for (int i = 1; i <= createGameDto.getNameOfPlayers().size(); i++) {
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

  @Override
  public List<GodModeDto> getAllGamesWithSecretInfos() {
    List<Game> games = gameRepository.findAll();
    List<GodModeDto> godModeDtoList = new ArrayList<>();
    games.forEach(game -> godModeDtoList.add(GodModeDto.builder()
        .id(game.getId())
        .uuid(game.getUuid())
        .drawDeck(getAvailableCards(game))
        .putAsideCard(game.getPutAsideCard())
        .publicCards(game.getPublicCards())
        .playersInGame(game.getPlayersInGame())
        .actualPlayer(game.getActualPlayer())
        .log(game.getLog())
        .hiddenLog(game.getHiddenLog())
        .isGameOver(game.getIsGameOver())
        .build()));
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
    } else throw new GameException(NO_GAME_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE + gameUuid);

    return statusDto;
  }

  @Override
  public PlayCardResponseDto playCard(PlayCardRequestDto requestDto) {
    PlayCardResponseDto responseDto = new PlayCardResponseDto();
    Player actualPlayer = playerService.findByUuid(requestDto.getPlayerUuid());
    if (actualPlayer != null) {
      Game game = findGameByPlayerUuid(actualPlayer.getUuid());
      if (game != null) {
        if (actualPlayer.getName().equals(game.getActualPlayer())) {
          if (hasPlayerTheCardSheOrHeWantToPlay(actualPlayer, requestDto.getCardName())) {
            if (requestDto.getCardName().matches(KING_OR_PRINCE_OR_BARON_OR_PRIEST_OR_GUARD_REGEX)) {
              if (!isThereAnyTargetablePlayer(actualPlayer, game)) {
                Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
                if (requestDto.getCardName().matches(KING_OR_BARON_OR_PRIEST_OR_GUARD_REGEX)) {
                  actualPlayer.discard(cardWantToPlayOut);
                  responseDto.setLastLog(addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(actualPlayer, cardWantToPlayOut, game));
                } else {
                  playOutPrince(actualPlayer, cardWantToPlayOut, responseDto, game);
                }
                setNextPlayerInOrder(actualPlayer, game);
                gameRepository.saveAndFlush(game);
              } else {
                if (requestDto.getAdditionalInfo() != null) {
                  Player targetPlayer = game.getPlayersInGame().stream()
                      .filter(p -> p.getName().equals(requestDto.getAdditionalInfo().getTargetPlayer()))
                      .findFirst().orElse(null);
                  if (targetPlayer != null) {
                    if (!targetPlayer.getName().equals(actualPlayer.getName())) {
                      if (!isTargetPlayersLastCardHandmaid(targetPlayer)) {
                        responseDto = processAdditionalInfo(actualPlayer, targetPlayer, game, requestDto);
                        setNextPlayerInOrder(actualPlayer, game);
                        gameRepository.saveAndFlush(game);
                      } else throw new GameException(PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE);
                    } else throw new GameException(PLAYER_SELF_TARGETING_ERROR_MESSAGE);
                  } else throw new GameException(PLAYER_NOT_FOUND_ERROR_MESSAGE);
                } else throw new GameException(PLAYER_NOT_SELECTED_ERROR_MESSAGE);
              }
            } else {
              responseDto = processAdditionalInfo(actualPlayer, null, game, requestDto);
              setNextPlayerInOrder(actualPlayer, game);
              gameRepository.saveAndFlush(game);
            }
          } else throw new GameException(HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE);
        } else throw new GameException(NOT_YOUR_TURN_ERROR_MESSAGE + actualPlayer.getName() + ".");
      } else throw new GameException(NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE);
    } else throw new GameException(NO_PLAYER_FOUND_WITH_GIVEN_UUID + requestDto.getPlayerUuid());
    return responseDto;
  }

  @Override
  public PlayerKnownInfosDto getAllInfosByPlayerUuid(String playerUuid) {
    Player player = playerRepository.findByUuid(playerUuid);
    PlayerKnownInfosDto knownInfosDto = new PlayerKnownInfosDto();
    if (player != null) {
      knownInfosDto.setMyName(player.getName());
      knownInfosDto.setNumberOfLetters(player.getNumberOfLetters());
      knownInfosDto.setCardsInHand(player.getCardsInHand());
      knownInfosDto.setPlayedCards(player.getPlayedCards());
      knownInfosDto.setGameLogsAboutMe(findGameLogsByPlayerUuidAndName(playerUuid, player.getName()));
      knownInfosDto.setGameHiddenLogsAboutMe(findGameHiddenLogsByPlayerUuidAndName(playerUuid, player.getName()));
      knownInfosDto.setAllGameLogs(findGameByPlayerUuid(playerUuid).getLog());
      knownInfosDto.setOtherPlayers(getOtherPlayersAndNumberOfLettersByPlayerUuid(playerUuid));
    } else throw new GameException(PLAYER_NOT_FOUND_ERROR_MESSAGE);
    return knownInfosDto;
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

  @Override
  public List<String> findGameHiddenLogsByPlayerUuidAndName(String uuid, String name) {
    Game game = findGameByPlayerUuid(uuid);
    return game.getHiddenLog().stream().filter(log -> log.contains(name)).collect(Collectors.toList());
  }

  private boolean isGivenNumberOfPlayersOutOfAllowedRange(CreateGameDto createGameDto) {
    int number = createGameDto.getNameOfPlayers().size();
    return number != 2 && number != 3 && number != 4;
  }

  private boolean isThereAreDuplicatedNamesInGivenPlayerNames(CreateGameDto createGameDto) {
    return createGameDto.getNameOfPlayers().stream().distinct()
        .count() != createGameDto.getNameOfPlayers().size();
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

  private void playOutPrince(Player player, Card cardWantToPlayOut,
      PlayCardResponseDto responseDto, Game game) {
    player.discard(cardWantToPlayOut);
    Card cardToDiscard = player.cardInHand();
    player.discard(cardToDiscard);
    if (cardToDiscard.getCardName().equals(PRINCESS)) {
      player.setIsInPlay(false);
      responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(player, game));
    } else {
      if (getAvailableCards(game).isEmpty()) {
        drawThePutAsideCard(player, game);
      } else {
        drawCard(player, game);
      }
      responseDto.setLastLog(addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(player, cardToDiscard, game));
    }
  }

  private boolean isThereAnyTargetablePlayer(Player actualPlayer, Game game) {
    List<Player> targetablePlayers = getActivePlayers(game);
    targetablePlayers = targetablePlayers.stream()
        .filter(player -> {
          if (player.getPlayedCards().isEmpty()) {
            return true;
          }
          return !player.lastPlayedCard().getCardName().equals(HANDMAID);
        })
        .collect(Collectors.toList());
    targetablePlayers.remove(actualPlayer);
    return !targetablePlayers.isEmpty();
  }

  private List<PlayerAndNumberOfLettersDto> getOtherPlayersAndNumberOfLettersByPlayerUuid(String uuid) {
    List<PlayerAndNumberOfLettersDto> dtoList = new ArrayList<>();

    findGameByPlayerUuid(uuid).getPlayersInGame().stream()
        .filter(player -> !player.getUuid().equals(uuid))
        .forEach(player -> {
          PlayerAndNumberOfLettersDto dto = new PlayerAndNumberOfLettersDto();
          dto.setPlayerName(player.getName());
          dto.setNumberOfLetters(player.getNumberOfLetters());
          dtoList.add(dto);
        });

    return dtoList;
  }

  private void setNextPlayerInOrder(Player actualPlayer, Game game) {
    if (isRoundOverBecauseThereIsOnlyOneActivePlayer(game)) {
      log.info("Round is over: there is only one player left.");
    } else if (isRoundOverBecauseDrawDeckIsEmptyAndThereAreAtLeastTwoActivePlayer(game)) {
      log.info("Round is over: the draw deck is empty.");
    } else {
      List<Player> activePlayers = getActivePlayers(game);

      Player nextActualPlayer;
      if (actualPlayer.getOrderNumber() == game.getPlayersInGame().size()) {
        nextActualPlayer = activePlayers.stream()
            .min(Comparator.comparing(Player::getOrderNumber)).orElse(null);
      } else {
        nextActualPlayer = activePlayers.stream()
            .filter(player -> player.getOrderNumber() > actualPlayer.getOrderNumber())
            .min(Comparator.comparingInt(Player::getOrderNumber)).orElse(null);
      }

      if (nextActualPlayer == null && getActivePlayers(game).size() >= 2) {
        nextActualPlayer = activePlayers.stream()
            .filter(player -> player.getOrderNumber() > 0)
            .min(Comparator.comparingInt(Player::getOrderNumber)).orElse(null);
      }

      if (nextActualPlayer != null) {
        game.setActualPlayer(nextActualPlayer.getName());
        game.addLog(ACTUAL_PLAYER_IS + game.getActualPlayer());
        drawCard(nextActualPlayer, game);
      } else throw new GameException("A játékossorrend rosszul került beállításra.");
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
          .cardNameEnglish(originalCard.getCardNameEnglish())
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

  private void drawThePutAsideCard(Player actualPlayer, Game game) {
    actualPlayer.getCardsInHand().add(game.getPutAsideCard());
  }

  private void addGameCreationLogs(Game game) {
    game.addLog(GAME_IS_CREATED_UUID + game.getUuid());
    game.addLog(PLAYERS_ARE + game.getPlayersInGame().stream().map(Player::getName)
        .collect(Collectors.joining(", ")));
    game.addLog(ACTUAL_PLAYER_IS + game.getActualPlayer());
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
      return targetPlayer.lastPlayedCard().getCardName().equals(HANDMAID);
    }
  }

  private PlayCardResponseDto processAdditionalInfo(Player actualPlayer, Player targetPlayer,
      Game game, PlayCardRequestDto requestDto) {
    PlayCardResponseDto responseDto = new PlayCardResponseDto();
    Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
    String cardNameWantToPlayOut = cardWantToPlayOut.getCardName();
    AdditionalInfoDto info = requestDto.getAdditionalInfo();

    switch (cardNameWantToPlayOut) {
      case PRINCESS:
        actualPlayer.discard(cardWantToPlayOut);
        actualPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerMustDiscardPrincess(actualPlayer, game));
        break;
      case COUNTESS:
        if (isCountessWithKingOrPrince(actualPlayer)) {
          throw new GameException(COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE);
        } else {
          actualPlayer.discard(cardWantToPlayOut);
          responseDto.setLastLog(addLogWhenAPlayerPlaysOutCountessOrHandmaid(actualPlayer, cardNameWantToPlayOut, game));
          break;
        }
      case HANDMAID:
        actualPlayer.discard(cardWantToPlayOut);
        responseDto.setLastLog(addLogWhenAPlayerPlaysOutCountessOrHandmaid(actualPlayer, cardNameWantToPlayOut, game));
        break;
      case KING:
        actualPlayer.discard(cardWantToPlayOut);
        Card actualPlayersCardInHand = actualPlayer.cardInHand();
        Card targetPlayersCardInHand = targetPlayer.cardInHand();
        actualPlayer.getCardsInHand().remove(actualPlayersCardInHand);
        actualPlayer.getCardsInHand().add(targetPlayersCardInHand);
        targetPlayer.getCardsInHand().remove(targetPlayersCardInHand);
        targetPlayer.getCardsInHand().add(actualPlayersCardInHand);
        responseDto.setLastLog(addLogWhenAPlayerUseKing(actualPlayer, targetPlayer, game));
        break;
      case PRINCE:
        if (targetPlayer.getName().equals(actualPlayer.getName())) {
          playOutPrince(actualPlayer, cardWantToPlayOut, responseDto, game);
          break;
        } else {
          actualPlayer.discard(cardWantToPlayOut);
          Card cardToDiscard = targetPlayer.cardInHand();
          targetPlayer.discard(cardToDiscard);
          if (cardToDiscard.getCardName().equals(PRINCESS)) {
            targetPlayer.setIsInPlay(false);
            responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, game));
          } else {
            drawCard(targetPlayer, game);
            responseDto.setLastLog(addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, cardToDiscard, game));
          }
        }
        break;
      case BARON:
        String cardNameOfActualPlayerToHiddenLog = cardNameInHandOf(actualPlayer);
        String cardNameOfTargetPlayerToHiddenLog = cardNameInHandOf(targetPlayer);
        actualPlayer.discard(cardWantToPlayOut);
        if (cardValueInHandOf(targetPlayer) < cardValueInHandOf(actualPlayer)) {
          Card cardToDiscard = targetPlayer.cardInHand();
          targetPlayer.discard(cardToDiscard);
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogWhenAPlayerUseBaronSuccessful(targetPlayer, cardToDiscard, actualPlayer, game));
        } else if (cardValueInHandOf(targetPlayer) > cardValueInHandOf(actualPlayer)) {
          Card cardToDiscard = actualPlayer.cardInHand();
          actualPlayer.discard(cardToDiscard);
          actualPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogWhenAPlayerUseBaronUnsuccessful(targetPlayer, cardToDiscard, actualPlayer, game));
        } else {
          responseDto.setLastLog(addLogWhenAPlayerUseBaronAndItsDraw(targetPlayer, actualPlayer, game));
        }
        game.addHiddenLog(actualPlayer.getName() + " (" + cardNameOfActualPlayerToHiddenLog + ")"
            + " és " + targetPlayer.getName() + " (" + cardNameOfTargetPlayerToHiddenLog + ")" +
            " összehasonlították a lapjaikat.");
        break;
      case PRIEST:
        actualPlayer.discard(cardWantToPlayOut);
        responseDto.setMessage(targetPlayer.getName() + " kezében egy " + cardNameInHandOf(targetPlayer) + " van.");
        responseDto.setLastLog(addLogWhenAPlayerUsePriest(actualPlayer, targetPlayer, game));
        break;
      case GUARD:
        actualPlayer.discard(cardWantToPlayOut);
        if (cardNameInHandOf(targetPlayer).equals(info.getNamedCard())) {
          Card cardToDiscard = targetPlayer.cardInHand();
          targetPlayer.discard(cardToDiscard);
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogWhenAPlayerUseGuardSuccessfully(requestDto, actualPlayer, targetPlayer, game));
        } else {
          responseDto.setLastLog(addLogWhenAPlayerUseGuardUnSuccessfully(requestDto, actualPlayer, targetPlayer, game));
        }
        break;
      default:
        throw new GameException("Played card is none of the predefined ones.");
    }

    return responseDto;
  }

  private boolean isCountessWithKingOrPrince(Player player) {
    String nameOfCards = player.getCardsInHand().stream().map(Card::getCardName).collect(Collectors.joining(" "));
    String otherCardName = nameOfCards.replace(COUNTESS, "");
    return otherCardName.equals(PRINCE) || otherCardName.equals(KING);
  }

  private Integer cardValueInHandOf(Player player) {
    return player.cardInHand().getCardValue();
  }

  private String cardNameInHandOf(Player player) {
    return player.cardInHand().getCardName();
  }

  private String addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(Player actualPlayer,
      Card cardWantToPlayOut, Game game) {
    String cardName = cardWantToPlayOut.getCardName();
    if (cardName.equals("Pap")) {
      cardName = "Papo";
    }
    return game.addLog(actualPlayer.getName() + " kijátszott egy " + cardName
        + "t, de mivel nem volt megcélozható játékos, nem történt semmi.");
  }

  private String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerPlaysOutCountessOrHandmaid(Player actualPlayer,
      String cardNameWantToPlayOut, Game game) {
    return game.addLog(actualPlayer.getName() + " kijátszott egy " + cardNameWantToPlayOut + "t.");
  }

  private String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " Királyt használva kártyát cserélt " + targetPlayer.getName()
            + " játékossal.");
  }

  private String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer,
      Game game) {
    return game
        .addLog(actualPlayer.getName() + " Herceggel eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(Player actualPlayer,
      Card cardToDiscard, Game game) {
    return game.addLog(
        actualPlayer.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy "
            + cardToDiscard.getCardName() + " volt.");
  }

  private String addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy Hercegnő volt, így " + targetPlayer.getName() + " kiesett a játékból.");
  }

  private String addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Card cardToDiscard, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy " + cardToDiscard.getCardName() + " volt.");
  }

  private String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer,
      Card cardToDiscard, Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + COMPARE_CARD_IN_HAND_WITH + targetPlayer.getName()
            + " kézben lévő lapjával. " + targetPlayer.getName()
            + " kiesett a játékból, kézben lévő lapját ("
            + cardToDiscard.getCardName() + ") pedig eldobta.");
  }

  private String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer,
      Card cardToDiscard, Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + COMPARE_CARD_IN_HAND_WITH + targetPlayer
            .getName()
            + " kézben lévő lapjával. " + actualPlayer.getName()
            + " kiesett a játékból, kézben lévő lapját ("
            + cardToDiscard.getCardName() + ") pedig eldobta.");
  }

  private String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer,
      Game game) {
    return game.addLog(
        actualPlayer.getName() + COMPARE_CARD_IN_HAND_WITH + targetPlayer
            .getName()
            + " kézben lévő lapjával. A lapok értéke azonos volt, így senki sem esett ki a játékból.");
  }

  private String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " megnézte, mi van " + targetPlayer.getName() + " kezében.");
  }

  private String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
            + " kezében " + namedCard + " van. Így igaz. "
            + targetPlayer.getName() + " kiesett a játékból.");
  }

  private String addLogWhenAPlayerUseGuardUnSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
            + " kezében " + namedCard + " van. Nem talált.");
  }

  private boolean isRoundOverBecauseThereIsOnlyOneActivePlayer(Game game) {
    boolean isRoundOver = false;
    List<Player> activePlayers = game.getPlayersInGame().stream()
        .filter(Player::getIsInPlay)
        .collect(Collectors.toList());

    if (activePlayers.size() == 1) {
      Player winner = activePlayers.get(0);
      game.addLog(ROUND_IS_OVER_ONLY_ONE_PLAYER_LEFT);
      game.addLog(winner.getName() + WON_THE_ROUND);
      winner.setNumberOfLetters(winner.getNumberOfLetters() + 1);

      giveAdditionalLoveLettersIfOnlyOneSpyIsActive(game);

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
        playersAndCardValuesInHand.put(player, cardValueInHandOf(player));
      }

      List<Player> winners = playersAndCardValuesInHand.entrySet().stream()
          .filter(entry -> entry.getValue().equals(Collections.max(playersAndCardValuesInHand.values())))
          .map(Entry::getKey)
          .collect(Collectors.toList());

      game.addLog(ROUND_IS_OVER_DRAW_DECK_IS_EMPTY);
      game.addLog(winners.stream()
          .map(Player::getName)
          .collect(Collectors.joining(" és ")) + WON_THE_ROUND);

      winners.forEach(winner -> winner.setNumberOfLetters(winner.getNumberOfLetters() + 1));

      giveAdditionalLoveLettersIfOnlyOneSpyIsActive(game);

      isRoundOver = true;
      checkLoveLettersAtRoundEnd(game);
    }

    return isRoundOver;
  }

  private void giveAdditionalLoveLettersIfOnlyOneSpyIsActive(Game game) {
    // TODO logika
  }

  private void checkLoveLettersAtRoundEnd(Game game) {
    if (isSomeoneHasEnoughLoveLettersToWinTheGame(game)) {
      game.setIsGameOver(true);
    } else {
      resetPlayers(game.getPlayersInGame());
      resetGame(game);
      game.addLog(NEW_ROUND_BEGINS_STATUS_MESSAGE + ACTUAL_PLAYER_IS + game.getActualPlayer());
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
      game.addLog(GAME_IS_OVER_STATUS_MESSAGE + " " + CONGRATULATE + winners.stream()
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
