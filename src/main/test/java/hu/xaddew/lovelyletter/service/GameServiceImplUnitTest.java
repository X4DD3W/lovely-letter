package hu.xaddew.lovelyletter.service;

import static hu.xaddew.lovelyletter.service.GameServiceImpl.COUNTESS;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.KING;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.NOT_YOUR_TURN_ERROR_MESSAGE;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE;
import static hu.xaddew.lovelyletter.service.GameServiceImpl.NO_PLAYER_FOUND_WITH_GIVEN_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static util.LLTestUtils.TWO_PLAYER_NUMBER;
import static util.LLTestUtils.UUID;
import static util.LLTestUtils.initCreateGameDto;
import static util.LLTestUtils.initCustomCards;
import static util.LLTestUtils.initGames;
import static util.LLTestUtils.initNewReleaseCards;
import static util.LLTestUtils.initOriginalCards;
import static util.LLTestUtils.initPlayers;
import static util.LLTestUtils.initTestPlayer;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.CreatedGameResponseDto;
import hu.xaddew.lovelyletter.dto.GameStatusDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.dto.PlayerKnownInfosDto;
import hu.xaddew.lovelyletter.dto.PlayerUuidDto;
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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import util.LLTestUtils;

@ExtendWith(MockitoExtension.class)
class GameServiceImplUnitTest {

  @Mock
  private Random random;

  @Spy
  private final ModelMapper modelMapper = new ModelMapper();

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

  private static List<Game> games;
  private static List<Player> players;
  private static List<OriginalCard> originalCards;
  private static List<CustomCard> customCards;
  private static List<NewReleaseCard> newReleaseCards;
  private static CreateGameDto createGameDto;
  private static PlayCardRequestDto playCardRequestDto;

  private Game resultGame;
  private List<GodModeDto> godModeDtoList;
  private CreatedGameResponseDto createdGameResponseDto;
  private List<String> playerNames;
  private GameException exception;

  @BeforeEach
  void init() {
    games = initGames(NUMBER_OF_PRE_GENERATED_GAMES);
    players = initPlayers(FOUR_PLAYER_NUMBER);
    originalCards = initOriginalCards(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS);
    newReleaseCards = initNewReleaseCards(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS);
    customCards = initCustomCards(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS);
  }

  @Test
  void testInitialization() {
    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, games.size());
  }

  @Test
  void getAllGamesWithSecretInfos() {
    when(gameRepository.findAll()).thenReturn(games);

    godModeDtoList = gameService.getAllGamesWithSecretInfos();

    verify(gameRepository).findAll();

    assertEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList.size());
    LLTestUtils.assertGeneratedValuesOfGamesAreEquals(NUMBER_OF_PRE_GENERATED_GAMES, godModeDtoList);
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

    resultGame = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNotNull(resultGame);
    assertEquals(resultGame, games.get(FIRST_INDEX));
  }

  @Test
  void findGameByPlayerUuidIfNoReturn() {
    String playerUuid = UUID + FIRST_INDEX;
    when(gameRepository.findGameByPlayerUuid(playerUuid)).thenReturn(null);

    resultGame = gameService.findGameByPlayerUuid(playerUuid);

    verify(gameRepository).findGameByPlayerUuid(playerUuid);

    assertNull(resultGame);
  }

  @Test
  void createGameThrowsGameExceptionIfDtoIsNull() {
    assertThrows(GameException.class, () -> gameService.createGame(null));
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsFiveAndOutOfRangeInClassicGame() {
    createGameDto = initCreateGameDto(List.of("A", "B", "C", "D", "E"), false);
    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsOneAndOutOfRangeInClassicGame() {
    createGameDto = initCreateGameDto(List.of("A"), false);
    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsSevenAndOutOfRangeIn2019VersionOfGame() {
    createGameDto = initCreateGameDto(List.of("A", "B", "C", "D", "E", "F", "G"), true);
    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameThrowsGameExceptionIfPlayerNumberIsOneAndOutOfRangeIn2019VersionOfGame() {
    createGameDto = initCreateGameDto(List.of("A"), true);
    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameThrowsGameExceptionIfThereIsDuplicatedPlayerNames() {
    createGameDto = initCreateGameDto(List.of("A", "A"), false);
    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameThrowsGameExceptionIfThereIsInvalidCustomCardInTheCustomCardNameList() {
    createGameDto = initCreateGameDto(List.of("A", "B"), false);
    createGameDto.setCustomCardNames(List.of(INVALID_CUSTOM_CARD_NAME));

    when(customCardService.findAll()).thenReturn(customCards);

    assertThrows(GameException.class, () -> gameService.createGame(createGameDto));
  }

  @Test
  void createGameInClassicVersionForFourPlayersWithoutCustomCards() {
    playerNames = List.of("A", "B", "C", "D");
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(10)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(originalCardService).findAll();
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForTwoPlayersWithoutCustomCards() {
    playerNames = List.of("A", "B");
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(9)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(originalCardService).findAll();
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForFourPlayersWithCustomCards() {
    playerNames = List.of("A", "B", "C", "D");
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(10)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(originalCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(OriginalCard.class));
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS +
        NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameInClassicVersionForTwoPlayersWithCustomCards() {
    playerNames = List.of("A", "B");
    createGameDto = initCreateGameDto(playerNames, false);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(originalCardService.findAll()).thenReturn(originalCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(9)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(originalCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(OriginalCard.class));
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS +
        NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForFourPlayersWithoutCustomCards() {
    playerNames = List.of("A", "B", "C", "D");
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(10)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(newReleaseCardService).findAll();
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForTwoPlayersWithoutCustomCards() {
    playerNames = List.of("A", "B");
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(new ArrayList<>());

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(9)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(newReleaseCardService).findAll();
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForFourPlayersWithCustomCards() {
    playerNames = List.of("A", "B", "C", "D");
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(10)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(newReleaseCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(NewReleaseCard.class));
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS +
        NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(FOUR_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void createGameIn2019VersionForTwoPlayersWithCustomCards() {
    playerNames = List.of("A", "B");
    createGameDto = initCreateGameDto(playerNames, true);
    createGameDto.setCustomCardNames(
        customCards.stream().map(CustomCard::getCardName).collect(Collectors.toList()));

    when(customCardService.findAll()).thenReturn(customCards);
    when(newReleaseCardService.findAll()).thenReturn(newReleaseCards);

    createdGameResponseDto = gameService.createGame(createGameDto);

    verify(random, times(9)).nextInt(anyInt());
    verify(customCardService, times(2)).findAll();
    verify(newReleaseCardService).findAll();
    verify(modelMapper, times(NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).map(any(), eq(NewReleaseCard.class));
    verify(cardService, times(NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS +
        NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS)).save(any());
    verify(gameRepository).save(any());
    verify(playerRepository).saveAll(any());

    assertNotNull(createdGameResponseDto);
    assertEquals(TWO_PLAYER_NUMBER, createdGameResponseDto.getPlayerUuidDtos().size());
    assertEquals(playerNames,
        createdGameResponseDto.getPlayerUuidDtos().stream().map(PlayerUuidDto::getName)
            .collect(Collectors.toList()));
  }

  @Test
  void playCardThrowsGameExceptionIfActualPlayerIsNotFound() {
    playCardRequestDto = PlayCardRequestDto.builder().playerUuid(UUID).build();

    when(playerService.findByUuid(UUID)).thenReturn(null);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(UUID);

    assertEquals(NO_PLAYER_FOUND_WITH_GIVEN_UUID + UUID, exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfGameIsNotFound() {
    Player player = initTestPlayer();
    playCardRequestDto = PlayCardRequestDto.builder().playerUuid(player.getUuid()).build();

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(null);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(player.getUuid());
    verify(gameRepository).findGameByPlayerUuid(player.getUuid());

    assertEquals(NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE, exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfPlayerIsNotTheActualPlayer() {
    Player player = initTestPlayer();
    playCardRequestDto = PlayCardRequestDto.builder().playerUuid(player.getUuid()).build();
    Game game = games.get(0);

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(player.getUuid());
    verify(gameRepository).findGameByPlayerUuid(player.getUuid());

    assertEquals(NOT_YOUR_TURN_ERROR_MESSAGE + player.getName() + ".", exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfPlayerHasNoCardWhatWantToPlayOut() {
    Player player = initTestPlayer();
    playCardRequestDto = PlayCardRequestDto.builder()
        .cardName(CARD_NAME)
        .playerUuid(player.getUuid())
        .build();
    Game game = games.get(0);
    game.setActualPlayer(player.getName());

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(player.getUuid());
    verify(gameRepository).findGameByPlayerUuid(player.getUuid());

    assertEquals(HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE, exception.getMessage());
  }

  @Test
  void playCardThrowsGameExceptionIfPlayerTryToPlayOutCountessInsteadOfPrinceOrKing() {
    Player player = initTestPlayer();
    playCardRequestDto = PlayCardRequestDto.builder()
        .cardName(KING)
        .playerUuid(player.getUuid())
        .build();
    Game game = games.get(0);
    game.setActualPlayer(player.getName());
    player.setCardsInHand(
        List.of(Card.builder().cardName(COUNTESS).build(), Card.builder().cardName(KING).build()));

    when(playerService.findByUuid(player.getUuid())).thenReturn(player);
    when(gameRepository.findGameByPlayerUuid(player.getUuid())).thenReturn(game);

    exception = assertThrows(GameException.class, () -> gameService.playCard(playCardRequestDto));

    verify(playerService).findByUuid(player.getUuid());
    verify(gameRepository).findGameByPlayerUuid(player.getUuid());

    assertEquals(COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE, exception.getMessage());
  }

}
