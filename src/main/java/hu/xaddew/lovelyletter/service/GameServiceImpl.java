package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.ResponseDto;
import hu.xaddew.lovelyletter.dto.PlayerAndNumberOfLettersDto;
import hu.xaddew.lovelyletter.dto.PlayerAndPlayedCardsDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PlayerUuidDto;
import hu.xaddew.lovelyletter.dto.PutBackCardsRequestDto;
import hu.xaddew.lovelyletter.exception.ErrorMessage;
import hu.xaddew.lovelyletter.exception.ErrorType;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.CustomCard;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.NewReleaseCard;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.time.LocalDateTime;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

  public static final String ACTUAL_PLAYER_IS = "Soron lévő játékos: ";
  public static final String GAME_IS_CREATED_UUID = "Játék létrehozva. Uuid: ";
  public static final String PLAYERS_ARE = "Játékosok: ";
  public static final String COMPARE_CARD_IN_HAND_WITH = " Báróval összehasonlította a kézben lévő lapját ";
  public static final String ROUND_IS_OVER_ONLY_ONE_PLAYER_LEFT = "A forduló véget ért, mert csak egy játékos maradt bent.";
  public static final String ROUND_IS_OVER_DRAW_DECK_IS_EMPTY = "A forduló véget ért, mert elfogyott a húzópakli.";
  public static final String WON_THE_ROUND = " nyerte a fordulót!";
  public static final String GAME_IS_OVER_STATUS_MESSAGE = "A játék véget ért, mivel valaki elég szerelmes levelet gyűjtött össze!";
  public static final String NEW_ROUND_BEGINS_STATUS_MESSAGE = "Új forduló kezdődik. ";
  public static final String CONGRATULATE = "Gratulálunk, ";

  public static final String PRINCESS = "Hercegnő";
  public static final String COUNTESS = "Grófnő";
  public static final String KING = "Király";
  public static final String CHANCELLOR = "Kancellár";
  public static final String PRINCE = "Herceg";
  public static final String HANDMAID = "Szobalány";
  public static final String BARON = "Báró";
  public static final String PRIEST = "Pap";
  public static final String GUARD = "Őr";
  public static final String SPY = "Kém";
  public static final String KILI = "Kili";

  public static final List<String> reservedNames = List.of(PRINCESS, COUNTESS, KING, CHANCELLOR,
      PRINCE, HANDMAID, BARON, PRIEST, GUARD, SPY, KILI);

  private int randomIndex;

  private final Random random;
  private final ModelMapper modelMapper;
  private final CardService cardService;
  private final OriginalCardService originalCardService;
  private final NewReleaseCardService newReleaseCardService;
  private final CustomCardService customCardService;
  private final PlayerService playerService;
  private final GameRepository gameRepository;
  private final PlayerRepository playerRepository;

  @Override
  public CreatedGameResponseDto createGame(CreateGameDto createGameDto) {
    if (createGameDto == null) {
      throw new GameException(ErrorMessage.MISSING_GAME_CREATE_REQUEST_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
    }

    boolean isGame2019Version = createGameDto.getIs2019Version();
    if (isGame2019Version) {
      if (isGivenNumberOfPlayersOutOfAllowedRangeIn2019Version(createGameDto)) {
        throw new GameException(ErrorMessage.PLAYER_NUMBER_IN_2019_VERSION_GAME_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
      }
    } else {
      if (isGivenNumberOfPlayersOutOfAllowedRangeInClassicVersion(createGameDto)) {
        throw new GameException(ErrorMessage.PLAYER_NUMBER_IN_CLASSIC_GAME_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
      }
    }

    if (isThereAreDuplicatedNamesInGivenPlayerNames(createGameDto)) {
      throw new GameException(ErrorMessage.PLAYER_NAME_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
    }

    if (isThereAreReservedNameInGivenPlayerNames(createGameDto)) {
      throw new GameException(ErrorMessage.RESERVED_NAMES_ERROR_MESSAGE, reservedNames.toString(), ErrorType.BAD_REQUEST);
    }

    if (isThereInvalidCustomCardInTheList(createGameDto.getCustomCardNames())) {
      throw new GameException(ErrorMessage.INVALID_CUSTOM_CARD_ERROR_MESSAGE, createGameDto.getCustomCardNames().toString(),
          ErrorType.BAD_REQUEST);
    }

    CreatedGameResponseDto responseDto = new CreatedGameResponseDto();
    Game game = new Game();
    List<PlayerUuidDto> playerUuidDtos = new ArrayList<>();
    List<Player> players = new ArrayList<>();

    game.setUuid(UUID.randomUUID().toString());

    createGameDto.getPlayerNames().forEach(name -> {
      PlayerUuidDto dto = new PlayerUuidDto();
      dto.setName(name);
      dto.setUuid(UUID.randomUUID().toString());
      playerUuidDtos.add(dto);
    });

    List<Integer> orderNumbers = new ArrayList<>();
    for (int i = 1; i <= createGameDto.getPlayerNames().size(); i++) {
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
    initDeckAndPutAsideCards(game, isGame2019Version, createGameDto.getCustomCardNames());
    dealOneCardToAllPlayers(game);
    determineStartPlayer(game);

    if (isGame2019Version) {
      game.setIs2019Version(true);
    }

    addGameCreationLogs(game);
    gameRepository.save(game);

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
        .drawDeck(game.getAvailableCards())
        .putAsideCard(game.getPutAsideCard())
        .publicCards(game.getPublicCards())
        .playersInGame(game.getPlayersInGame())
        .actualPlayer(game.getActualPlayer())
        .log(game.getLog())
        .hiddenLog(game.getHiddenLog())
        .isGameOver(game.getIsGameOver())
        .is2019Version(game.getIs2019Version())
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
      statusDto.setNumberOfCardsInDrawDeck(game.getAvailableCards().size());
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
    } else throw new GameException(ErrorMessage.NO_GAME_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE, gameUuid, ErrorType.NOT_FOUND);

    return statusDto;
  }

  @Override
  public ResponseDto playCard(PlayCardRequestDto requestDto) {
    ResponseDto responseDto = new ResponseDto();
    Player actualPlayer = playerService.findByUuid(requestDto.getPlayerUuid());
    if (actualPlayer != null) {
      Game game = findGameByPlayerUuid(actualPlayer.getUuid());
      if (game != null) {
        if (actualPlayer.getName().equals(game.getActualPlayer())) {
          if (hasPlayerTheCardSheOrHeWantToPlay(actualPlayer, requestDto.getCardName())) {
            if (isCountessWithKingOrPrince(actualPlayer) && !requestDto.getCardName().equals(COUNTESS)) {
              throw new GameException(ErrorMessage.COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE, ErrorType.CONFLICT);
            }
            if (requestDto.getCardName().matches(KING  + "|" + BARON + "|" + PRIEST + "|" + GUARD + "|" + PRINCE)) {
              if (isNotThereOtherTargetablePlayer(actualPlayer, game)) {
                Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
                if (requestDto.getCardName().matches(KING  + "|" + BARON + "|" + PRIEST + "|" + GUARD)) {
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
                      if (targetPlayer.getName().equals(actualPlayer.getName()) && requestDto.getCardName().matches(PRINCE)) {
                        Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
                        playOutPrince(actualPlayer, cardWantToPlayOut, responseDto, game);
                        setNextPlayerInOrder(actualPlayer, game);
                        gameRepository.saveAndFlush(game);
                      } else {
                        if (!targetPlayer.getName().equals(actualPlayer.getName())) {
                          if (isPlayerInPlay(targetPlayer)) {
                            if (!isTargetPlayerLastCardHandmaid(targetPlayer)) {
                              responseDto = processAdditionalInfo(actualPlayer, targetPlayer, game, requestDto);
                              setNextPlayerInOrder(actualPlayer, game);
                              gameRepository.saveAndFlush(game);
                            } else throw new GameException(ErrorMessage.PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
                          } else throw new GameException(ErrorMessage.PLAYER_IS_ALREADY_OUT_OF_ROUND_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
                        } else throw new GameException(ErrorMessage.PLAYER_SELF_TARGETING_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
                      }
                    } else throw new GameException(ErrorMessage.PLAYER_NOT_FOUND_ERROR_MESSAGE, ErrorType.NOT_FOUND);
                  } else throw new GameException(ErrorMessage.PLAYER_NOT_SELECTED_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
              }
            } else {
              Player targetPlayer = new Player();
              responseDto = processAdditionalInfo(actualPlayer, targetPlayer, game, requestDto);
              setNextPlayerInOrder(actualPlayer, game);
              gameRepository.saveAndFlush(game);
            }
          } else throw new GameException(ErrorMessage.HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
        } else throw new GameException(ErrorMessage.NOT_YOUR_TURN_ERROR_MESSAGE, actualPlayer.getName() + ".", ErrorType.BAD_REQUEST);
      } else throw new GameException(ErrorMessage.NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE, ErrorType.NOT_FOUND);
    } else throw new GameException(ErrorMessage.NO_PLAYER_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE, requestDto.getPlayerUuid(), ErrorType.NOT_FOUND);
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

      Game game = findGameByPlayerUuid(playerUuid);
      if (game != null) {
        knownInfosDto.setGameLogsAboutMe(getGameLogsByPlayerName(player.getName(), game));
        knownInfosDto.setGameHiddenLogsAboutMe(getGameHiddenLogsByPlayerName(player.getName(), game));
        knownInfosDto.setAllGameLogs(game.getLog());
        knownInfosDto.setOtherPlayers(getOtherPlayersAndNumberOfLettersByPlayerUuidAndGame(playerUuid, game));
      }

    } else throw new GameException(ErrorMessage.PLAYER_NOT_FOUND_ERROR_MESSAGE, ErrorType.NOT_FOUND);
    return knownInfosDto;
  }

  @Override
  public Game findGameByPlayerUuid(String playerUuid) {
    return gameRepository.findGameByPlayerUuid(playerUuid);
  }

  @Override
  public ResponseDto putBackCards(PutBackCardsRequestDto requestDto) {
    if (requestDto == null) {
      throw new GameException(ErrorMessage.MISSING_PUT_BACK_A_CARD_REQUEST_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
    }
    ResponseDto responseDto = new ResponseDto();
    Player actualPlayer = playerService.findByUuid(requestDto.getPlayerUuid());
    if (actualPlayer != null) {
      Game game = findGameByPlayerUuid(actualPlayer.getUuid());
      if (game != null) {
        if (actualPlayer.getName().equals(game.getActualPlayer())) {
          if (hasPlayerTheCardsSheOrHeWantToPutBack(actualPlayer, requestDto.getCardsToPutBack())) {
            List<Card> cardsWantToPutBack = new ArrayList<>();
            requestDto.getCardsToPutBack().forEach(cardName -> {
              Card cardWantToPutBack = cardService.getCardAtPlayerByCardName(actualPlayer, cardName);
              cardsWantToPutBack.add(cardWantToPutBack);
              actualPlayer.getCardsInHand().remove(cardWantToPutBack);
              cardWantToPutBack.setIsAtAPlayer(false);
              game.getDrawDeck().add(cardWantToPutBack);

            });
            if (!cardsWantToPutBack.contains(null) && cardsWantToPutBack.size() == requestDto
                .getCardsToPutBack().size() && actualPlayer.hasOnlyOneCardInHand()) {
              responseDto.setLastLog(addLogWhenAPlayerUseChancellorToPutBackCards(actualPlayer, requestDto.getCardsToPutBack().size(), game));
              game.setIsTurnOfChancellorActive(false);
              setNextPlayerInOrder(actualPlayer, game);
              gameRepository.saveAndFlush(game);
            } else throw new GameException(ErrorMessage.PUT_BACK_CARDS_GENERAL_ERROR_MESSAGE, ErrorType.INTERNAL_SERVER_ERROR);
          } else throw new GameException(ErrorMessage.HAVE_NO_CARDS_WHAT_WANT_TO_PUT_BACK_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
        } else throw new GameException(ErrorMessage.NOT_YOUR_TURN_ERROR_MESSAGE, actualPlayer.getName() + ".", ErrorType.BAD_REQUEST);
      } else throw new GameException(ErrorMessage.NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE, ErrorType.NOT_FOUND);
    } else throw new GameException(ErrorMessage.NO_PLAYER_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE, requestDto.getPlayerUuid(), ErrorType.NOT_FOUND);

    return responseDto;
  }

  @Override
  @Transactional
  public void closeOpenGamesInactiveFor(LocalDateTime modifyDate) {
    List<Game> games = gameRepository.findAllByIsGameOverFalseAndModifyDateGreaterThan(modifyDate);
    games.forEach(game -> {
      game.addLog("Game is closed because of inactivity.");
      game.setIsGameOver(true);
    });
    log.info("Number of closed games: {}", games.size());
  }

  @Override
  @Transactional
  public void deleteClosedGamesOlderThan(LocalDateTime modifyDate) {
    gameRepository.deleteAllByIsGameOverTrueAndModifyDateGreaterThan(modifyDate);
  }

  public List<String> getGameLogsByPlayerName(String name, Game game) {
    return game.getLog().stream().filter(log -> log.contains(name)).collect(Collectors.toList());
  }

  public List<String> getGameHiddenLogsByPlayerName(String name, Game game) {
    return game.getHiddenLog().stream().filter(log -> log.contains(name)).collect(Collectors.toList());
  }

  private boolean isGivenNumberOfPlayersOutOfAllowedRangeIn2019Version(CreateGameDto createGameDto) {
    int number = createGameDto.getPlayerNames().size();
    return number != 2 && number != 3 && number != 4 && number != 5 && number != 6;
  }

  private boolean isGivenNumberOfPlayersOutOfAllowedRangeInClassicVersion(CreateGameDto createGameDto) {
    int number = createGameDto.getPlayerNames().size();
    return number != 2 && number != 3 && number != 4;
  }

  private boolean isThereAreDuplicatedNamesInGivenPlayerNames(CreateGameDto createGameDto) {
    return createGameDto.getPlayerNames().stream().distinct()
        .count() != createGameDto.getPlayerNames().size();
  }

  private boolean isThereAreReservedNameInGivenPlayerNames(CreateGameDto createGameDto) {
    return !Collections.disjoint(reservedNames, createGameDto.getPlayerNames());
  }

  private boolean isThereInvalidCustomCardInTheList(List<String> customCardNames) {
    List<CustomCard> customCards = customCardService.findAll();
    return !customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()).containsAll(customCardNames);
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

  private void playOutPrince(Player player, Card cardWantToPlayOut, ResponseDto responseDto, Game game) {
    player.discard(cardWantToPlayOut);
    Card cardToDiscard = player.cardInHand();
    player.discard(cardToDiscard);
    if (cardToDiscard.getCardName().equals(PRINCESS)) {
      player.setIsInPlay(false);
      responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(player, game));
    } else {
      if (game.getAvailableCards().isEmpty()) {
        drawThePutAsideCard(player, game);
      } else {
        drawCard(player, game);
      }
      responseDto.setLastLog(addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(player, cardToDiscard, game));
    }
  }

  private boolean isNotThereOtherTargetablePlayer(Player actualPlayer, Game game) {
    List<Player> targetablePlayers = game.getActivePlayers();
    targetablePlayers = targetablePlayers.stream()
        .filter(player -> {
          if (player.getPlayedCards().isEmpty()) {
            return true;
          }
          return !player.lastPlayedCard().getCardName().equals(HANDMAID);
        })
        .collect(Collectors.toList());
    targetablePlayers.remove(actualPlayer);
    return targetablePlayers.isEmpty();
  }

  private List<PlayerAndNumberOfLettersDto> getOtherPlayersAndNumberOfLettersByPlayerUuidAndGame(String uuid, Game game) {
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

  private void setNextPlayerInOrder(Player actualPlayer, Game game) {
    if (isRoundOverBecauseThereIsOnlyOneActivePlayer(game)) {
      log.info("Round is over: there is only one player left.");
    } else if (isRoundOverBecauseDrawDeckIsEmptyAndThereAreAtLeastTwoActivePlayer(game)) {
      log.info("Round is over: the draw deck is empty.");
    } else {
      List<Player> activePlayers = game.getActivePlayers();

      Player nextActualPlayer;

      if (game.isTurnOfChancellorActive()) {
        game.addLog(ACTUAL_PLAYER_IS + actualPlayer.getName() + ", aki éppen a Kancellár által húzott lapokból választ...");
      } else {
        if (actualPlayer.getOrderNumber() == game.getPlayersInGame().size()) {
          nextActualPlayer = activePlayers.stream()
              .min(Comparator.comparing(Player::getOrderNumber)).orElse(null);
        } else {
          nextActualPlayer = activePlayers.stream()
              .filter(player -> player.getOrderNumber() > actualPlayer.getOrderNumber())
              .min(Comparator.comparingInt(Player::getOrderNumber)).orElse(null);
        }

        if (nextActualPlayer == null && game.getActivePlayers().size() >= 2) {
          nextActualPlayer = activePlayers.stream()
              .filter(player -> player.getOrderNumber() > 0)
              .min(Comparator.comparingInt(Player::getOrderNumber)).orElse(null);
        }

        if (nextActualPlayer != null) {
          game.setActualPlayer(nextActualPlayer.getName());
          game.addLog(ACTUAL_PLAYER_IS + game.getActualPlayer());
          drawCard(nextActualPlayer, game);
        } else throw new GameException(ErrorMessage.PLAYER_ORDER_ERROR_MESSAGE, ErrorType.INTERNAL_SERVER_ERROR);
      }
    }
  }

  private void initDeckAndPutAsideCards(Game game, boolean is2019Version, List<String> customCardNames) {
    List<Card> drawDeck;
    List<CustomCard> customCardsInPlay = customCardService.findAll().stream()
        .filter(customCard -> customCardNames.contains(customCard.getCardName()))
        .collect(Collectors.toList());

    if (is2019Version) {
      List<NewReleaseCard> cardsFromDatabase = newReleaseCardService.findAll();
      if (!customCardsInPlay.isEmpty()) {
        cardsFromDatabase.addAll(mapList(customCardsInPlay, NewReleaseCard.class));
      }
      drawDeck = createNewDrawDeckFromNewReleaseCards(cardsFromDatabase);
    } else {
      List<OriginalCard> cardsFromDatabase = originalCardService.findAll();
      if (!customCardsInPlay.isEmpty()) {
        cardsFromDatabase.addAll(mapList(customCardsInPlay, OriginalCard.class));
      }
      drawDeck = createNewDrawDeckFromOriginalCards(cardsFromDatabase);
    }
    game.setDrawDeck(drawDeck);
    drawDeck.forEach(card -> card.setGame(game));
    putAsideCards(game);
  }

  private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    return source.stream().map(e -> modelMapper.map(e, targetClass)).collect(Collectors.toList());
  }

  private List<Card> createNewDrawDeckFromNewReleaseCards(List<NewReleaseCard> newReleaseCards) {
    List<Card> deck = new LinkedList<>();
    for (NewReleaseCard newReleaseCard : newReleaseCards) {
      Card card = Card.builder()
          .cardName(newReleaseCard.getCardName())
          .cardNameEnglish(newReleaseCard.getCardNameEnglish())
          .cardValue(newReleaseCard.getCardValue())
          .quantity(newReleaseCard.getQuantity())
          .description(newReleaseCard.getDescription())
          .isPutAside(newReleaseCard.getIsPutAside())
          .is2PlayerPublic(newReleaseCard.getIs2PlayerPublic())
          .isAtAPlayer(newReleaseCard.getIsAtAPlayer())
          .build();
      deck.add(card);
    }
    return deck;
  }

  private List<Card> createNewDrawDeckFromOriginalCards(List<OriginalCard> originalCards) {
    List<Card> deck = new LinkedList<>();
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
      deck.add(card);
    }
    return deck;
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
    randomIndex = random.nextInt(game.getAvailableCards().size());
    Card cardToPutAside = game.getAvailableCards().get(randomIndex);
    cardToPutAside.setIsPutAside(true);
  }

  private void drawACardToMakePublicInTwoPlayerMode(Game game) {
    randomIndex = random.nextInt(game.getAvailableCards().size());
    Card cardToMakePublic = game.getAvailableCards().get(randomIndex);
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

  private void drawCard(Player player, Game game) {
    List<Card> availableCards = game.getAvailableCards();
    if (!availableCards.isEmpty()) {
      randomIndex = random.nextInt(availableCards.size());
      Card drawnCard = availableCards.get(randomIndex);
      player.getCardsInHand().add(drawnCard);
      drawnCard.setIsAtAPlayer(true);
    }
  }

  private void drawThePutAsideCard(Player player, Game game) {
    player.getCardsInHand().add(game.getPutAsideCard());
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

  private boolean isPlayerInPlay(Player player) {
    return player.getIsInPlay();
  }

  private boolean isTargetPlayerLastCardHandmaid(Player targetPlayer) {
    if (targetPlayer.getPlayedCards().isEmpty()) {
      return false;
    } else {
      return targetPlayer.lastPlayedCard().getCardName().equals(HANDMAID);
    }
  }

  private ResponseDto processAdditionalInfo(Player actualPlayer, Player targetPlayer,
      Game game, PlayCardRequestDto requestDto) {
    ResponseDto responseDto = new ResponseDto();
    Card cardWantToPlayOut = cardService.getCardAtPlayerByCardName(actualPlayer, requestDto.getCardName());
    String cardNameWantToPlayOut = cardWantToPlayOut.getCardName();
    AdditionalInfoDto info = requestDto.getAdditionalInfo();
    Card cardToDiscard;

    if (requestDto.getCardName().equals(GUARD) && info.getNamedCard().equals(GUARD)) {
      throw new GameException(ErrorMessage.GUARD_IS_NOT_TARGETED_WITH_GUARD_ERROR_MESSAGE, ErrorType.BAD_REQUEST);
    }

    switch (cardNameWantToPlayOut) {
      case PRINCESS:
        actualPlayer.discard(cardWantToPlayOut);
        actualPlayer.setIsInPlay(false);
        responseDto.setLastLog(addLogWhenAPlayerMustDiscardPrincess(actualPlayer, game));
        break;
      case COUNTESS:
      case HANDMAID:
      case SPY:
      case KILI:
        actualPlayer.discard(cardWantToPlayOut);
        responseDto.setLastLog(addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpy(actualPlayer, cardNameWantToPlayOut, game));
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
      case CHANCELLOR:
        actualPlayer.discard(cardWantToPlayOut);
        if (game.getAvailableCards().isEmpty()) {
          responseDto.setLastLog(addLogWhenAPlayerUseChancellorToDrawZeroCard(actualPlayer, game));
          break;
        }
        List<Card> drawnCardsByChancellor = drawCardsBecauseOfChancellor(actualPlayer, game);
        game.setIsTurnOfChancellorActive(true);
        responseDto.setMessage("A Kancellárral húzott lap(ok): " + drawnCardsByChancellor.stream()
            .map(Card::getCardName).collect(Collectors.joining(", ")) + ".");
        responseDto.setLastLog(addLogWhenAPlayerUseChancellorToDrawOneOrTwoCards(actualPlayer, game, drawnCardsByChancellor.size()));
        break;
      case PRINCE:
        actualPlayer.discard(cardWantToPlayOut);
        cardToDiscard = targetPlayer.cardInHand();
        targetPlayer.discard(cardToDiscard);
        if (cardToDiscard.getCardName().equals(PRINCESS)) {
          targetPlayer.setIsInPlay(false);
          responseDto.setLastLog(addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, game));
        } else {
          if (game.getAvailableCards().isEmpty()) {
            drawThePutAsideCard(targetPlayer, game);
          } else {
            drawCard(targetPlayer, game);
          }
          responseDto.setLastLog(addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(actualPlayer, targetPlayer, cardToDiscard, game));
        }
        break;
      case BARON:
        String cardNameOfActualPlayerToHiddenLog = cardNameInHandOf(actualPlayer);
        String cardNameOfTargetPlayerToHiddenLog = cardNameInHandOf(targetPlayer);
        actualPlayer.discard(cardWantToPlayOut);
        if (cardValueInHandOf(targetPlayer) < cardValueInHandOf(actualPlayer)) {
          cardToDiscard = targetPlayer.cardInHand();
          targetPlayer.discard(cardToDiscard);
          if (cardToDiscard.getCardName().equals(KILI)) {
            drawCard(targetPlayer, game);
            responseDto.setLastLog(addLogWhenAPlayerShouldDiscardKiliByBaron(targetPlayer, cardToDiscard, actualPlayer, game));
          } else {
            targetPlayer.setIsInPlay(false);
            responseDto.setLastLog(addLogWhenAPlayerUseBaronSuccessful(targetPlayer, cardToDiscard, actualPlayer, game));
          }
        } else if (cardValueInHandOf(targetPlayer) > cardValueInHandOf(actualPlayer)) {
          cardToDiscard = actualPlayer.cardInHand();
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
        responseDto.setMessage(targetPlayer.getName() + " kezében " + cardNameInHandOf(targetPlayer) + " van.");
        responseDto.setLastLog(addLogWhenAPlayerUsePriest(actualPlayer, targetPlayer, game));
        break;
      case GUARD:
        actualPlayer.discard(cardWantToPlayOut);
        if (cardNameInHandOf(targetPlayer).equals(info.getNamedCard())) {
          cardToDiscard = targetPlayer.cardInHand();
          targetPlayer.discard(cardToDiscard);
          if (cardToDiscard.getCardName().equals(KILI)) {
            drawCard(targetPlayer, game);
            responseDto.setLastLog(addLogWhenAPlayerShouldDiscardKiliByGuard(requestDto, actualPlayer, targetPlayer, game));
          } else {
            targetPlayer.setIsInPlay(false);
            responseDto.setLastLog(addLogWhenAPlayerUseGuardSuccessfully(requestDto, actualPlayer, targetPlayer, game));
          }
        } else {
          responseDto.setLastLog(
              addLogWhenAPlayerUseGuardUnsuccessfully(requestDto, actualPlayer, targetPlayer, game));
        }
        break;
      default:
        throw new GameException(ErrorMessage.PLAYED_CARD_IS_NOT_PREDEFINED_ONES_ERROR_MESSAGE, ErrorType.INTERNAL_SERVER_ERROR);
    }

    return responseDto;
  }

  private boolean isCountessWithKingOrPrince(Player player) {
    String nameOfCards = player.getCardsInHand().stream().map(Card::getCardName).collect(Collectors.joining(" "));
    String otherCardName = nameOfCards.replace(COUNTESS, "").trim();
    return otherCardName.equals(PRINCE) || otherCardName.equals(KING);
  }

  private List<Card> drawCardsBecauseOfChancellor(Player player, Game game) {
    List<Card> drawnCardsByChancellor = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      List<Card> availableCards = game.getAvailableCards();
      if (!availableCards.isEmpty()) {
        randomIndex = random.nextInt(availableCards.size());
        Card drawnCard = availableCards.get(randomIndex);
        player.getCardsInHand().add(drawnCard);
        drawnCardsByChancellor.add(drawnCard);
        drawnCard.setIsAtAPlayer(true);
      }
    }

    return drawnCardsByChancellor;
  }

  private Integer cardValueInHandOf(Player player) {
    return player.cardInHand().getCardValue();
  }

  private String cardNameInHandOf(Player player) {
    return player.cardInHand().getCardName();
  }

  private String addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(Player actualPlayer,
      Card cardWantToPlayOut, Game game) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott lapja egy " + cardWantToPlayOut.getCardName()
            + " volt, de megcélozható játékos híján nem történt semmi.");
  }

  private String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
  }

  private String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott egy Királyt, ő és " + targetPlayer.getName()
            + " kártyát cseréltek.");
  }

  private String addLogWhenAPlayerUseChancellorToDrawZeroCard(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName()
        + " kijátszott egy Kancellárt, de mivel a húzópakli üres volt, a kártyának nincsen hatása.");
  }

  private String addLogWhenAPlayerUseChancellorToDrawOneOrTwoCards(Player actualPlayer, Game game,
      int numberOfDrawnCards) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott egy Kancellárt, amivel felhúzta a pakli felső "
            + numberOfDrawnCards + " lapját. A kezében lévő " + (numberOfDrawnCards + 1)
            + " lapból egyet meg kell tartania, a többit pedig visszatennie a pakli aljára.");
  }

  private String addLogWhenAPlayerUseChancellorToPutBackCards(Player actualPlayer, int numberOfCards, Game game) {
    return game.addLog(actualPlayer.getName() + ", miután Kancellárral húzott " + numberOfCards
        + " lapot, visszatett ugyanennyit a pakliba.");
  }

  private String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer,
      Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobta a Hercegnőt, így kiesett a játékból.");
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

  private String addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpy(Player actualPlayer,
      String cardName, Game game) {
    return game.addLog(actualPlayer.getName() + " kijátszott lapja egy " + cardName + " volt.");
  }

  private String addLogWhenAPlayerShouldDiscardKiliByBaron(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + COMPARE_CARD_IN_HAND_WITH + targetPlayer.getName()
        + " kézben lévő lapjával. " + targetPlayer.getName()
        + " kézben lévő lapja " + cardToDiscard.getCardName()
        + " volt, aki megmentett gazdáját a kiesétől ("
        + targetPlayer.getName() + " húzott egy új lapot).");
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

  private String addLogWhenAPlayerShouldDiscardKiliByGuard(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
        + " kezében " + namedCard + " van. Így igaz. "
        + targetPlayer.getName() + " eldobta a lapját és ahelyett, hogy kiesett volna,"
        + " húzott egy új lapot.");
  }

  private String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
            + " kezében " + namedCard + " van. Így igaz. "
            + targetPlayer.getName() + " kiesett a játékból.");
  }

  private String addLogWhenAPlayerUseGuardUnsuccessfully(PlayCardRequestDto requestDto,
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
      winner.addOneLetter();

      giveAdditionalLoveLetterIfOnlyOneSpyIsActive(game);

      isRoundOver = true;
      checkLoveLettersAtRoundEnd(game);
    }

    return isRoundOver;
  }

  private boolean isRoundOverBecauseDrawDeckIsEmptyAndThereAreAtLeastTwoActivePlayer(Game game) {
    boolean isRoundOver = false;
    List<Card> availableCards = game.getAvailableCards();

    if (availableCards.isEmpty() && !game.isTurnOfChancellorActive()) {
      List<Player> activePlayers = game.getActivePlayers();

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

      winners.forEach(Player::addOneLetter);

      giveAdditionalLoveLetterIfOnlyOneSpyIsActive(game);

      isRoundOver = true;
      checkLoveLettersAtRoundEnd(game);
    }

    return isRoundOver;
  }

  private void giveAdditionalLoveLetterIfOnlyOneSpyIsActive(Game game) {
    List<Player> playersWhoPlayedSpyAndAreInPlay = new ArrayList<>();

    game.getPlayersInGame().stream()
        .filter(Player::getIsInPlay)
        .filter(player -> player.getPlayedCards().stream()
            .map(Card::getCardName)
            .collect(Collectors.joining())
            .contains(SPY))
        .forEach(playersWhoPlayedSpyAndAreInPlay::add);

    if (playersWhoPlayedSpyAndAreInPlay.size() == 1) {
      playersWhoPlayedSpyAndAreInPlay.get(0).addOneLetter();
    }
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
      requiredLetters = game.is2019Version() ? 6 : 7;
    } else if (game.getPlayersInGame().size() == 3) {
      requiredLetters = 5;
    } else if (game.getPlayersInGame().size() == 4) {
      requiredLetters = 4;
    } else {
      requiredLetters = 3;
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

  private boolean hasPlayerTheCardsSheOrHeWantToPutBack(Player player, List<String> cardsToPutBack) {
    return player.getCardsInHand().stream()
        .map(Card::getCardName)
        .collect(Collectors.toList())
        .containsAll(cardsToPutBack);
  }
}
