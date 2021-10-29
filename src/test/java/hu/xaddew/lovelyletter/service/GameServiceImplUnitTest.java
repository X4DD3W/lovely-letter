package hu.xaddew.lovelyletter.service;

import static hu.xaddew.lovelyletter.service.GameServiceImpl.BARON;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.CHANCELLOR;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.COUNTESS;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.GUARD;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.HANDMAID;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.KING;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.PRIEST;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.PRINCE;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.PRINCESS;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.ROUND_IS_OVER_DRAW_DECK_IS_EMPTY;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.SPY;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.WON_THE_ROUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.LLTestUtils.ACTUAL_PLAYER;
import static util.LLTestUtils.CARD_NAME;
import static util.LLTestUtils.FIRST_INDEX;
import static util.LLTestUtils.FOUR_PLAYER_NUMBER;
import static util.LLTestUtils.INVALID_CUSTOM_CARD_NAME;
import static util.LLTestUtils.INVALID_UUID;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_GAMES;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS;
import static util.LLTestUtils.NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS;
import static util.LLTestUtils.PLAYER_NAME;
import static util.LLTestUtils.THREE_PLAYER_NUMBER;
import static util.LLTestUtils.TWO_PLAYER_NUMBER;
import static util.LLTestUtils.UNIVERSAL_NUMBER;
import static util.LLTestUtils.UUID;
import static util.LLTestUtils.getPlayerNamesOf;
import static util.LLTestUtils.initCreateGameDto;
import static util.LLTestUtils.initCustomCards;
import static util.LLTestUtils.initGames;
import static util.LLTestUtils.initNewReleaseCards;
import static util.LLTestUtils.initOriginalCards;
import static util.LLTestUtils.initTestPlayer;
import static util.LLTestUtils.assertGeneratedValuesOfGamesAreEquals;

import hu.xaddew.lovelyletter.dto.AdditionalInfoDto;
import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PlayerUuidDto;
import hu.xaddew.lovelyletter.dto.ResponseDto;
import hu.xaddew.lovelyletter.exception.ErrorMessage;
import hu.xaddew.lovelyletter.exception.GameException;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.CustomCard;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.NewReleaseCard;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.model.Player;
import hu.xaddew.lovelyletter.repository.GameRepository;
import hu.xaddew.lovelyletter.repository.PlayerRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class GameServiceImplUnitTest {

  @Spy
  private final ModelMapper modelMapper = new ModelMapper();

  @Mock
  private Random random;

  @Mock
  private CardServiceImpl cardService;

  @Mock
  private OriginalCardServiceImpl originalCardService;

  @Mock
  private NewReleaseCardService newReleaseCardService;

  @Mock
  private CustomCardService customCardService;

  @Mock
  private PlayerServiceImpl playerService;

  @Mock
  private GameRepository gameRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private GameServiceImpl gameService;

  private List<Game> games;
  private List<Player> players;
  private Game game;
  private Player player;
  private Player targetPlayer;
  private Card cardToPlayOut;
  private Card otherCardAtActualPlayer;
  private Card cardAtTargetPlayer;
  private AdditionalInfoDto infoDto;
  private List<OriginalCard> originalCards;
  private List<CustomCard> customCards;
  private List<NewReleaseCard> newReleaseCards;
  private CreateGameDto createGameDto;
  private PlayCardRequestDto playCardRequestDto;
  private List<GodModeDto> godModeDtoList;
  private CreatedGameResponseDto createdGameResponseDto;
  private List<String> playerNames;
  private GameException exception;
  private ResponseDto responseDto;
  private String generatedLog;

  @BeforeEach
  void init() {
    games = initGames(NUMBER_OF_PRE_GENERATED_GAMES, THREE_PLAYER_NUMBER);
    players = games.get(0).getPlayersInGame();
    originalCards = initOriginalCards(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS);
    newReleaseCards = initNewReleaseCards(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS);
    customCards = initCustomCards(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS);
    infoDto = new AdditionalInfoDto();
  }

  @Test
  void testInitialization() {
    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, games.size());
    assertEquals(THREE_PLAYER_NUMBER, players.size());
    assertEquals(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS, originalCards.size());
    assertEquals(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS, newReleaseCards.size());
    assertEquals(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS, customCards.size());
  }

  @Test
  void getAllGamesWithSecretInfos() {
    when(gameRepository.findAll()).thenReturn(games);

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList.size());
    assertGeneratedValuesOfGamesAreEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList);
  }

  @Test
  void getAllGamesWithSecretInfosIfListIsEmpty() {
    when(gameRepository.findAll()).thenReturn(new ArrayList<>());

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertTrue(godModeDtoList.isEmpty());
  }

  @Test
  void findAll() {
    when(gameRepository.findAll()).thenReturn(games);

    List<Game> result = gameService.findAll();

    verify(gameRepository).findAll();

    assertNotNull(result);
    assertEquals(result, games);
  }

  @Test
  void findAllIfNoReturn() {
    when(gameRepository.findAll()).thenReturn(new ArrayList<>());

    List<Game> result = gameService.findAll();

    verify(gameRepository).findAll();

    assertTrue(result.isEmpty());
  }

  @Test
  void getGameStatus() {
    String gameUuid = UUID + FIRST_INDEX;
    games.get(FIRST_INDEX).setPlayersInGame(players);
    when(gameRepository.findByUuid(gameUuid)).thenReturn(games.get(FIRST_INDEX));

    GameStatusDto gameStatusDto = gameService.getGameStatus(gameUuid);

    verify(gameRepository).findByUuid(gameUuid);

    assertNotNull(gameStatusDto);
    assertEquals(ACTUAL_PLAYER, gameStatusDto.getActualPlayer());
  }

  @Test
  void getGameStatusThrowsGameException() {
    assertThrows(GameException.class, () -> gameService.getGameStatus(INVALID_UUID));
  }

  @Test
  void getAllInfosByPlayerUuid() {
    String playerUuid = UUID + FIRST_INDEX;
    when(playerRepository.findByUuid(playerUuid)).thenReturn(players.get(FIRST_INDEX));
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(games.get(FIRST_INDEX));

    PlayerKnownInfosDto playerKnownInfosDto = gameService.getAllInfosByPlayerUuid(playerUuid);

    verify(playerRepository).findByUuid(playerUuid);
    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNotNull(playerKnownInfosDto);
  }

  @Test
  void getAllInfosByPlayerUuidThrowsGameException() {
    assertThrows(GameException.class, () -> gameService.getAllInfosByPlayerUuid(INVALID_UUID));
  }

  @Test
  void findGameByPlayerUuid() {
    String playerUuid = UUID + FIRST_INDEX;
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(games.get(FIRST_INDEX));

    game = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNotNull(game);
    assertEquals(game, games.get(FIRST_INDEX));
  }

  @Test
  void findGameByPlayerUuidIfNoReturn() {
    String playerUuid = UUID + FIRST_INDEX;
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(null);

    game = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNull(game);
  }

  @Test
  void createGameThrowsGameExceptionIfDtoIsNull() {
    exception = assertThrows(GameException.class, () -> gameService.createGame(null));
    assertEquals(ErrorMessage.MISSING_GAME_CREATE_REQUEST_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsFiveAndOutOfRangeInClassicGame() {
    createGameDto = initCreateGameDto(getPlayerNamesOf(5), false);
    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
    assertEquals(ErrorMessage.PLAYER_NUMBER_IN_CLASSIC_GAME_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsOneAndOutOfRangeInClassicGame() {
    createGameDto = initCreateGameDto(getPlayerNamesOf(1), false);
    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
    assertEquals(ErrorMessage.PLAYER_NUMBER_IN_CLASSIC_GAME_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsSevenAndOutOfRangeIn2019VersionOfGame() {
    createGameDto = initCreateGameDto(getPlayerNamesOf(7), true);
    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
    assertEquals(ErrorMessage.PLAYER_NUMBER_IN_2019_VERSION_GAME_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsOneAndOutOfRangeIn2019VersionOfGame() {
    createGameDto = initCreateGameDto(getPlayerNamesOf(1), true);
    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
    assertEquals(ErrorMessage.PLAYER_NUMBER_IN_2019_VERSION_GAME_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfThereIsDuplicatedPlayerNames() {
    createGameDto = initCreateGameDto(List.of("A", "A"), false);
    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
    assertEquals(ErrorMessage.PLAYER_NAME_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void createGameThrowsGameExceptionIfThereIsInvalidCustomCardInTheCustomCardNameList() {
    createGameDto = initCreateGameDto(getPlayerNamesOf(2), false);
    List<String> customCardNames = List.of(INVALID_CUSTOM_CARD_NAME);
    createGameDto.setCustomCardNames(customCardNames);

    when(customCardService.findAll()).thenReturn(customCards);

    exception = assertThrows(GameException.class, () -> gameService.createGame(createGameDto));

    verify(customCardService).findAll();

    assertEquals(ErrorMessage.INVALID_CUSTOM_CARD_ERROR_MESSAGE.toString() + customCardNames, exception.getMessage());
  }

  @Test
  void createGameInClassicVersionForFourPlayersWithoutCustomCards() {
    playerNames = getPlayerNamesOf(4);
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(10);
    verify(originalCardService).findAll();

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForTwoPlayersWithoutCustomCards() {
    playerNames = getPlayerNamesOf(2);
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(9);
    verify(originalCardService).findAll();

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForFourPlayersWithCustomCards() {
    playerNames = getPlayerNamesOf(4);
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(10);
    verify(originalCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(OriginalCard.class));

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForTwoPlayersWithCustomCards() {
    playerNames = getPlayerNamesOf(2);
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(9);
    verify(originalCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(OriginalCard.class));

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForFourPlayersWithoutCustomCards() {
    playerNames = getPlayerNamesOf(4);
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(10);
    verify(newReleaseCardService).findAll();

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForTwoPlayersWithoutCustomCards() {
    playerNames = getPlayerNamesOf(2);
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(9);
    verify(newReleaseCardService).findAll();

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForFourPlayersWithCustomCards() {
    playerNames = getPlayerNamesOf(4);
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(10);
    verify(newReleaseCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(NewReleaseCard.class));

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForTwoPlayersWithCustomCards() {
    playerNames = getPlayerNamesOf(2);
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verifyGameCreationCommonInvocations(9);
    verify(newReleaseCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(NewReleaseCard.class));

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void playCardThrowsGameExceptionIfActualPlayerIsNotFound() {
    playCardRequestDto = new PlayCardRequestDto(UUID);

    when(playerService.findByUuid(UUID)).thenReturn(null);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(UUID);

    assertEquals(ErrorMessage.NO_PLAYER_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE + UUID, exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfGameIsNotFound() {
    player = initTestPlayer();
    playCardRequestDto = new PlayCardRequestDto(player.getUuid());

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(null);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfPlayerIsNotTheActualPlayer() {
    player = initTestPlayer();
    game = games.get(0);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid());

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.NOT_YOUR_TURN_ERROR_MESSAGE + player.getName() + ".", exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfPlayerHasNoCardWhatWantToPlayOut() {
    player = initTestPlayer();
    game = games.get(0);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), CARD_NAME);
    game.setActualPlayer(player.getName());

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, PRINCE})
  void playCardThrowsGameExceptionIfPlayerTryToPlayOutCountessInsteadOfKingOrPrince(String cardName) {
    player = initTestPlayer();
    game = games.get(0);
    game.setActualPlayer(player.getName());
    setCardsAtActualPlayer(new Card(COUNTESS), new Card(cardName));
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndThereIsNoOtherTargetablePlayer(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName);
    players.forEach(p -> p.getPlayedCards().add(new Card(HANDMAID)));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, playCardRequestDto.getCardName()))
        .thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " kijátszott lapja egy " + cardName + " volt,"
        + " de megcélozható játékos híján nem történt semmi.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertEquals(1, player.getCardsInHand().size());
    assertNotEquals(player.getName(), game.getActualPlayer());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceInsteadOfPrincessAndThereIsNoOtherTargetablePlayer() {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRINCESS);
    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE);

    players.forEach(p -> p.getPlayedCards().add(new Card(HANDMAID)));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, playCardRequestDto.getCardName())).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobta a Hercegnőt, így kiesett a játékból.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getPlayedCards().contains(otherCardAtActualPlayer));
    assertTrue(player.getCardsInHand().isEmpty());
    assertFalse(player.getIsInPlay());
    assertNotEquals(player.getName(), game.getActualPlayer());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceInsteadOfNonPrincessCardAndThereIsNoOtherTargetablePlayerAndDrawThePutAsideCard() {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = player.cardInHand();

    setPutAsideCard(CARD_NAME, UNIVERSAL_NUMBER);

    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE);
    players.forEach(p -> p.getPlayedCards().add(new Card(HANDMAID)));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, playCardRequestDto.getCardName()))
        .thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy "
        + otherCardAtActualPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(gameRepository).save(game);
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(game.getLog().contains(generatedLog));
    assertTrue(game.getLog().contains("2. " + ROUND_IS_OVER_DRAW_DECK_IS_EMPTY));
    assertTrue(game.getLog().contains("3. " + player.getName() + WON_THE_ROUND));
    assertFalse(game.getIsGameOver());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceInsteadOfGuardAndThereIsNoOtherTargetablePlayerAndDrawFromDeck() {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = player.cardInHand();
    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE);
    players.forEach(p -> p.getPlayedCards().add(new Card(HANDMAID)));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, playCardRequestDto.getCardName()))
        .thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy "
        + otherCardAtActualPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getPlayedCards().contains(otherCardAtActualPlayer));
    assertEquals(1, player.getCardsInHand().size());
    assertNotEquals(player.getName(), game.getActualPlayer());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceInsteadOfOtherPrinceAndDrawFromDeck() {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRINCE);
    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE,
        new AdditionalInfoDto(player.getName(), PRINCE));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, playCardRequestDto.getCardName()))
        .thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy "
        + otherCardAtActualPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getPlayedCards().contains(otherCardAtActualPlayer));
    assertEquals(1, player.getCardsInHand().size());
    assertNotEquals(player.getName(), game.getActualPlayer());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardThrowsIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndAdditionalInfoIsEmpty(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.PLAYER_NOT_SELECTED_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndTargetPlayerIsNotFound(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    infoDto.setTargetPlayer(PLAYER_NAME);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName, infoDto);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.PLAYER_NOT_FOUND_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndTargetPlayerIsSelf(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    infoDto.setTargetPlayer(player.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName, infoDto);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.PLAYER_SELF_TARGETING_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndTargetPlayerIsOutOfGame(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    infoDto.setTargetPlayer(PLAYER_NAME + 2);
    players.get(1).setIsInPlay(false);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName, infoDto);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.PLAYER_IS_ALREADY_OUT_OF_ROUND_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {KING, BARON, PRIEST, GUARD})
  void playCardIfPlayerPlayOutKingOrBaronOrPriestOrGuardAndTargetPlayersLastCardIsHandmaid(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    infoDto.setTargetPlayer(PLAYER_NAME + 2);
    players.get(1).getPlayedCards().add(new Card(HANDMAID));
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName, infoDto);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void playCardIfPlayerPlayOutPrincess() {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(PRINCESS);
    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCESS);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRINCESS)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " eldobta a Hercegnőt, így kiesett a játékból.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRINCESS);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertFalse(player.getIsInPlay());
  }

  @ParameterizedTest
  @ValueSource(strings = {COUNTESS, HANDMAID, SPY})
  void playCardIfPlayerPlayOutCountessOrHandmaidOrSpy(String cardName) {
    player = players.get(0);
    game = games.get(0);
    cardToPlayOut = new Card(cardName);
    player.getCardsInHand().add(cardToPlayOut);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardName);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, cardName)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " kijátszott lapja egy " + cardName + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, cardName);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
  }

  @Test
  void playCardIfPlayerPlayOutKing() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(KING);
    otherCardAtActualPlayer = new Card(PRIEST);
    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), KING, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, KING)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog =
        "1. " + player.getName() + " kijátszott egy Királyt, ő és " + targetPlayer.getName()
            + " kártyát cseréltek.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, KING);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getCardsInHand().contains(cardAtTargetPlayer));
    assertTrue(targetPlayer.getCardsInHand().contains(otherCardAtActualPlayer));
  }

  @Test
  void playCardIfPlayerPlayOutPrinceWithSelfTarget() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRIEST);

    infoDto.setTargetPlayer(player.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRINCE)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy " +
        otherCardAtActualPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRINCE);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertFalse(player.getCardsInHand().contains(otherCardAtActualPlayer));
  }

  @Test
  void playCardIfPlayerPlayOutPrinceAndTargetPlayerHasPrincess() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRIEST);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = new Card(PRINCESS);
    setCardsAtTargetPlayer(cardAtTargetPlayer);

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRINCE)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy Hercegnő volt, így " + targetPlayer.getName() + " kiesett a játékból.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRINCE);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(targetPlayer.getPlayedCards().contains(cardAtTargetPlayer));
    assertFalse(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceAndTargetPlayerHasNotPrincessAndDrawDeckIsNotEmpty() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRIEST);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRINCE)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy " + cardAtTargetPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRINCE);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(targetPlayer.getPlayedCards().contains(cardAtTargetPlayer));
    assertTrue(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutPrinceAndTargetPlayerHasNotPrincessAndDrawDeckIsEmpty() {
    player = players.get(0);
    game = games.get(0);

    setPutAsideCard(KING, 5);

    cardToPlayOut = new Card(PRINCE);
    otherCardAtActualPlayer = new Card(PRIEST, 2);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRINCE, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRINCE)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy " + cardAtTargetPlayer.getCardName() + " volt.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRINCE);
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(game.getLog().contains(generatedLog));
    assertEquals(1, targetPlayer.getNumberOfLetters());
  }

  @Test
  void playCardIfPlayerPlayOutBaronAndWinTheCompare() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(BARON);
    otherCardAtActualPlayer = new Card(PRIEST, 2);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), BARON, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, BARON)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Báróval összehasonlította a kézben lévő lapját " +
        targetPlayer.getName() + " kézben lévő lapjával. " + targetPlayer.getName()
        + " kiesett a játékból, kézben lévő lapját ("
        + cardAtTargetPlayer.getCardName() + ") pedig eldobta.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, BARON);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getCardsInHand().contains(otherCardAtActualPlayer));
    assertTrue(targetPlayer.getPlayedCards().contains(cardAtTargetPlayer));
    assertFalse(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutBaronAndLostTheCompare() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(BARON);
    otherCardAtActualPlayer = new Card(PRIEST, 0);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), BARON, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, BARON)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Báróval összehasonlította a kézben lévő lapját " +
        targetPlayer.getName() + " kézben lévő lapjával. " + player.getName()
        + " kiesett a játékból, kézben lévő lapját ("
        + otherCardAtActualPlayer.getCardName() + ") pedig eldobta.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, BARON);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getPlayedCards().contains(otherCardAtActualPlayer));
    assertTrue(targetPlayer.getCardsInHand().contains(cardAtTargetPlayer));
    assertFalse(player.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutBaronAndItIsDraw() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(BARON);
    otherCardAtActualPlayer = new Card(PRIEST, 1);

    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), BARON, infoDto);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, BARON)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Báróval összehasonlította a kézben lévő lapját " +
        targetPlayer.getName() + " kézben lévő lapjával. "
        + "A lapok értéke azonos volt, így senki sem esett ki a játékból.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, BARON);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(player.getCardsInHand().contains(otherCardAtActualPlayer));
    assertTrue(targetPlayer.getCardsInHand().contains(cardAtTargetPlayer));
    assertTrue(player.getIsInPlay());
    assertTrue(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutPriest() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(PRIEST);
    targetPlayer = players.get(1);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), PRIEST, infoDto);

    setCardsAtActualPlayer(cardToPlayOut);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, PRIEST)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog =
        "1. " + player.getName() + " megnézte, mi van " + targetPlayer.getName() + " kezében.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, PRIEST);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(targetPlayer.getCardsInHand().contains(cardAtTargetPlayer));
  }

  @Test
  void playCardIfPlayerPlayOutGuardAndUseSuccessfully() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(GUARD);
    targetPlayer = players.get(1);

    cardAtTargetPlayer = new Card(BARON);
    setCardsAtTargetPlayer(cardAtTargetPlayer);

    infoDto.setTargetPlayer(targetPlayer.getName());
    infoDto.setNamedCard(BARON);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), GUARD, infoDto);

    setCardsAtActualPlayer(cardToPlayOut);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, GUARD)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog =
        "1. " + player.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
            + " kezében " + cardAtTargetPlayer.getCardName() + " van. Így igaz. "
            + targetPlayer.getName() + " kiesett a játékból.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, GUARD);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(targetPlayer.getPlayedCards().contains(cardAtTargetPlayer));
    assertFalse(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutGuardAndUseUnsuccessfully() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(GUARD);
    targetPlayer = players.get(1);

    cardAtTargetPlayer = new Card(BARON);
    setCardsAtTargetPlayer(cardAtTargetPlayer);

    infoDto.setTargetPlayer(targetPlayer.getName());
    infoDto.setNamedCard(KING);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), GUARD, infoDto);

    setCardsAtActualPlayer(cardToPlayOut);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, GUARD)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog =
        "1. " + player.getName() + " Őrt játszott ki. Szerinte " + targetPlayer.getName()
            + " kezében " + infoDto.getNamedCard() + " van. Nem talált.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, GUARD);
    verify(gameRepository).saveAndFlush(game);

    assertCardPlayingCommonAssertions();
    assertTrue(targetPlayer.getCardsInHand().contains(cardAtTargetPlayer));
    assertTrue(targetPlayer.getIsInPlay());
  }

  @Test
  void playCardIfPlayerPlayOutGuardAndGuessGuard() {
    player = players.get(0);
    game = games.get(0);

    cardToPlayOut = new Card(GUARD);
    targetPlayer = players.get(1);
    cardAtTargetPlayer = new Card(BARON);
    setCardsAtTargetPlayer(cardAtTargetPlayer);

    infoDto.setTargetPlayer(targetPlayer.getName());
    infoDto.setNamedCard(GUARD);
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), GUARD, infoDto);

    setCardsAtActualPlayer(cardToPlayOut);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, GUARD)).thenReturn(cardToPlayOut);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verifyCardPlayingCommonInvocations(player.getUuid());

    assertEquals(ErrorMessage.GUARD_IS_NOT_TARGETED_WITH_GUARD_ERROR_MESSAGE.toString(), exception.getMessage());
  }

  @Test
  void playCardIfPlayerPlayOutChancellorAndDrawZeroCard() {
    player = players.get(0);
    game = games.get(0);
    game.getDrawDeck().forEach(card -> card.setIsAtAPlayer(true));

    cardToPlayOut = new Card(CHANCELLOR);
    otherCardAtActualPlayer = new Card(PRIEST, 2);

    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), CHANCELLOR);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, CHANCELLOR)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName()
        + " kijátszott egy Kancellárt, de mivel a húzópakli üres volt, a kártyának nincsen hatása.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, CHANCELLOR);
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(game.getLog().contains(generatedLog));
  }

  @Test
  void playCardIfPlayerPlayOutChancellorAndDrawOneCard() {
    player = players.get(0);
    game = games.get(0);

    Card drawnByChancellor = new Card(HANDMAID, 4);
    drawnByChancellor.setIsAtAPlayer(false);
    drawnByChancellor.setIs2PlayerPublic(false);
    drawnByChancellor.setIsPutAside(false);

    List<Card> drawDeck = new ArrayList<>();
    drawDeck.add(drawnByChancellor);
    game.setDrawDeck(drawDeck);

    cardToPlayOut = new Card(CHANCELLOR);
    otherCardAtActualPlayer = new Card(PRIEST, 2);

    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), CHANCELLOR);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, CHANCELLOR)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName()
        + " kijátszott egy Kancellárt, amivel felhúzta a pakli felső 1 lapját. A kezében lévő 2"
        + " lapból egyet meg kell tartania, a többit pedig visszatennie a pakli aljára.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, CHANCELLOR);
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(responseDto.getMessage().contains(drawnByChancellor.getCardName()));
    assertTrue(game.getLog().contains(generatedLog));
    assertTrue(game.getIsTurnOfChancellorActive());
  }

  @Test
  void playCardIfPlayerPlayOutChancellorAndDrawTwoCards() {
    player = players.get(0);
    game = games.get(0);

    Card drawnByChancellor = new Card(HANDMAID, 4);
    drawnByChancellor.setIsAtAPlayer(false);
    drawnByChancellor.setIs2PlayerPublic(false);
    drawnByChancellor.setIsPutAside(false);

    Card drawnByChancellor2 = new Card(HANDMAID, 4);
    drawnByChancellor2.setIsAtAPlayer(false);
    drawnByChancellor2.setIs2PlayerPublic(false);
    drawnByChancellor2.setIsPutAside(false);

    List<Card> drawDeck = new ArrayList<>();
    drawDeck.add(drawnByChancellor);
    drawDeck.add(drawnByChancellor2);
    game.setDrawDeck(drawDeck);

    cardToPlayOut = new Card(CHANCELLOR);
    otherCardAtActualPlayer = new Card(PRIEST, 2);

    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), CHANCELLOR);

    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, CHANCELLOR)).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName()
        + " kijátszott egy Kancellárt, amivel felhúzta a pakli felső 2 lapját. A kezében lévő 3"
        + " lapból egyet meg kell tartania, a többit pedig visszatennie a pakli aljára.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, CHANCELLOR);
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(responseDto.getMessage().contains(drawnByChancellor.getCardName()));
    assertTrue(responseDto.getMessage().contains(drawnByChancellor2.getCardName()));
    assertTrue(game.getLog().contains(generatedLog));
    assertTrue(game.getIsTurnOfChancellorActive());
  }

  @Test
  void playCardAndRoundEndsBecauseThereIsOnlyOneActivePlayer() {
    List<Player> playersInGame = new ArrayList<>();
    player = players.get(0);
    targetPlayer = players.get(1);
    playersInGame.add(player);
    playersInGame.add(targetPlayer);

    game = games.get(0);
    game.setPlayersInGame(playersInGame);

    cardToPlayOut = new Card(BARON, 3);
    otherCardAtActualPlayer = new Card(PRINCESS, 8);
    setCardsAtActualPlayer(cardToPlayOut, otherCardAtActualPlayer);
    cardAtTargetPlayer = targetPlayer.cardInHand();

    infoDto.setTargetPlayer(targetPlayer.getName());
    playCardRequestDto = new PlayCardRequestDto(player.getUuid(), cardToPlayOut.getCardName(), infoDto);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);
    when(cardService.getCardAtPlayerByCardName(player, cardToPlayOut.getCardName())).thenReturn(cardToPlayOut);

    responseDto = gameService.playCard(playCardRequestDto);

    generatedLog = "1. " + player.getName() + " Báróval összehasonlította a kézben lévő lapját " +
        targetPlayer.getName() + " kézben lévő lapjával. " + targetPlayer.getName()
        + " kiesett a játékból, kézben lévő lapját ("
        + cardAtTargetPlayer.getCardName() + ") pedig eldobta.";

    verifyCardPlayingCommonInvocations(player.getUuid());
    verify(cardService).getCardAtPlayerByCardName(player, cardToPlayOut.getCardName());
    verify(gameRepository).saveAndFlush(game);

    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(game.getLog().contains(generatedLog));
  }

  private void verifyGameCreationCommonInvocations(int times) {
    verify(random, times(times)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(gameRepository).save(any());
  }

  private void verifyCardPlayingCommonInvocations(String uuid) {
    verify(playerService).findByUuid(uuid);
    verify(gameRepository).findGameByPlayerUuid(uuid);
  }

  private void assertCardPlayingCommonAssertions() {
    assertNotNull(responseDto);
    assertEquals(generatedLog, responseDto.getLastLog());
    assertTrue(game.getLog().contains(generatedLog));
    assertTrue(player.getPlayedCards().contains(cardToPlayOut));
  }

  private void setPutAsideCard(String cardName, int cardValue) {
    List<Card> drawDeck = new ArrayList<>();
    Card putAsideCard = new Card(cardName, cardValue);
    putAsideCard.setIsPutAside(true);
    drawDeck.add(putAsideCard);
    game.setDrawDeck(drawDeck);
  }

  private void setCardsAtActualPlayer(Card ...cards) {
    List<Card> cardsInHand = new ArrayList<>(Arrays.asList(cards));
    player.setCardsInHand(cardsInHand);
  }

  private void setCardsAtTargetPlayer(Card ...cards) {
    List<Card> cardsInHand = new ArrayList<>(Arrays.asList(cards));
    targetPlayer.setCardsInHand(cardsInHand);
  }
}
