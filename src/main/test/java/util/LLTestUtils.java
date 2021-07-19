package util;

import static hu.xaddew.lovelyletter.service.GameServiceImpl.GUARD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.xaddew.lovelyletter.dto.CreateGameDto;
import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.CustomCard;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.NewReleaseCard;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.model.Player;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LLTestUtils {

  public static final int NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS = 10;
  public static final int NUMBER_OF_PRE_GENERATED_CUSTOM_CARDS = 3;
  public static final int NUMBER_OF_PRE_GENERATED_NEW_RELEASE_CARDS = 10;
  public static final int NUMBER_OF_PRE_GENERATED_GAMES = 3;
  public static final int TWO_PLAYER_NUMBER = 2;
  public static final int THREE_PLAYER_NUMBER = 3;
  public static final int FOUR_PLAYER_NUMBER = 4;
  public static final int FIRST_INDEX = 1;
  public static final int UNIVERSAL_NUMBER = 77;
  public static final String CARD_NAME = "cardName";
  public static final String INVALID_CUSTOM_CARD_NAME = "invalidCustomCardName";
  public static final String CARD_DESCRIPTION = "Description";
  public static final String ACTUAL_PLAYER = "playerName1";
  public static final String UUID = "UUID";
  public static final String INVALID_UUID = "invalidUuid";
  public static final String PLAYER_NAME = "playerName";
  public static final List<String> PLAYER_NAMES = List.of("A", "B", "C", "D", "E", "F", "G");

  public static List<Game> initGames(int numberOfGames, int numberOfPlayers) {
    List<Game> games = new ArrayList<>();
    for (int i = 1; i <= numberOfGames; i++) {
      List<Card> drawDeck = List.of(initCardInDrawDeck(CARD_NAME));
      List<Player> players = initPlayers(numberOfPlayers);
      games.add(Game.builder()
          .id((long) i)
          .uuid(UUID + i)
          .drawDeck(drawDeck)
          .playersInGame(players)
          .actualPlayer(players.get(0).getName())
          .log(new LinkedList<>())
          .hiddenLog(new LinkedList<>())
          .isGameOver(false)
          .is2019Version(false)
          .isTurnOfChancellorActive(false)
          .build());
    }
    return games;
  }

  public static Card initCardInDrawDeck(String cardName) {
    return Card.builder()
        .id((long) UNIVERSAL_NUMBER)
        .cardName(cardName)
        .cardValue(UNIVERSAL_NUMBER)
        .quantity(UNIVERSAL_NUMBER)
        .description(CARD_DESCRIPTION)
        .is2PlayerPublic(false)
        .isAtAPlayer(false)
        .isPutAside(false)
        .build();
  }

  public static List<Player> initPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    for (int i = 1; i <= numberOfPlayers; i++) {
      List<Card> cardsInHand = new ArrayList<>();
      cardsInHand.add(new Card(GUARD));
      players.add(Player.builder()
          .id((long) i)
          .uuid(UUID + i)
          .name(PLAYER_NAME + i)
          .cardsInHand(cardsInHand)
          .playedCards(new ArrayList<>())
          .numberOfLetters(0)
          .isInPlay(true)
          .orderNumber(i)
          .build());
    }
    return players;
  }

  public static List<OriginalCard> initOriginalCards(int numberOfOriginalCards) {
    List<OriginalCard> originalCards = new ArrayList<>();
    for (int i = 1; i <= numberOfOriginalCards; i++) {
      originalCards.add(OriginalCard.builder()
          .id((long) i)
          .cardName(CARD_NAME + i)
          .cardValue(numberOfOriginalCards - i)
          .quantity(i)
          .description(CARD_DESCRIPTION + i)
          .isAtAPlayer(false)
          .isPutAside(false)
          .is2PlayerPublic(false)
          .build());
    }
    return originalCards;
  }

  public static List<NewReleaseCard> initNewReleaseCards(int numberOfNewReleaseCards) {
    List<NewReleaseCard> newReleaseCards = new ArrayList<>();
    for (int i = 1; i <= numberOfNewReleaseCards; i++) {
      newReleaseCards.add(NewReleaseCard.builder()
          .id((long) i)
          .cardName(CARD_NAME + i)
          .cardValue(numberOfNewReleaseCards - i)
          .quantity(i)
          .description(CARD_DESCRIPTION + i)
          .is2PlayerPublic(false)
          .isAtAPlayer(false)
          .isPutAside(false)
          .build());
    }
    return newReleaseCards;
  }

  public static List<CustomCard> initCustomCards(int numberOfCustomCards) {
    List<CustomCard> customCards = new ArrayList<>();
    for (int i = 1; i <= numberOfCustomCards; i++) {
      customCards.add(CustomCard.builder()
          .id((long) i)
          .cardName(CARD_NAME + i)
          .cardValue(numberOfCustomCards - i)
          .quantity(i)
          .description(CARD_DESCRIPTION + i)
          .is2PlayerPublic(false)
          .isAtAPlayer(false)
          .isPutAside(false)
          .build());
    }
    return customCards;
  }

  public static List<Card> initCards(int numberOfCards) {
    List<Card> cards = new ArrayList<>();
    for (int i = 1; i <= numberOfCards; i++) {
      cards.add(Card.builder()
          .id((long) i)
          .cardName(CARD_NAME + i)
          .cardValue(numberOfCards - i)
          .quantity(i)
          .description(CARD_DESCRIPTION + i)
          .is2PlayerPublic(false)
          .isAtAPlayer(false)
          .isPutAside(false)
          .build());
    }
    return cards;
  }

  public static Player initTestPlayer() {
    return Player.builder()
        .id((long) UNIVERSAL_NUMBER)
        .uuid(String.valueOf(UNIVERSAL_NUMBER))
        .name(PLAYER_NAME + UNIVERSAL_NUMBER)
        .cardsInHand(new ArrayList<>())
        .playedCards(new ArrayList<>())
        .numberOfLetters(0)
        .isInPlay(true)
        .orderNumber(1)
        .build();
  }

  public static CreateGameDto initCreateGameDto(List<String> playerNames, boolean is2019Version) {
    CreateGameDto createGameDto = new CreateGameDto();
    createGameDto.setPlayerNames(playerNames);
    createGameDto.setIs2019Version(is2019Version);
    return createGameDto;
  }

  public static void assertGeneratedValuesOfGamesAreEquals(int numberOfPreGeneratedGames, List<GodModeDto> godModeDtoList) {
    for (int i = 1; i <= numberOfPreGeneratedGames ; i++) {
      GodModeDto actualDto = godModeDtoList.get(i - 1);
      assertEquals(i, actualDto.getId());
      assertEquals(UUID + i, actualDto.getUuid());
      assertEquals(ACTUAL_PLAYER, actualDto.getActualPlayer());
    }
  }

  public static List<String> getPlayerNamesOf(int number) {
    return PLAYER_NAMES.subList(0, number);
  }
}
