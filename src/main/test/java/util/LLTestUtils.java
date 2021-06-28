package util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.xaddew.lovelyletter.dto.GodModeDto;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.OriginalCard;
import hu.xaddew.lovelyletter.model.Player;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LLTestUtils {

  public static final int NUMBER_OF_PRE_GENERATED_ORIGINAL_CARDS = 10;
  public static final int NUMBER_OF_PRE_GENERATED_GAMES = 3;
  public static final int FOUR_PLAYERS = 4;
  public static final int FIRST_INDEX = 1;
  public static final String CARD_NAME = "cardName";
  public static final String CARD_DESCRIPTION = "Description";
  public static final String ACTUAL_PLAYER = "actualPlayer";
  public static final String UUID = "UUID";
  public static final String INVALID_UUID = "invalidUuid";
  public static final String PLAYER_NAME = "playerName";

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

  public static List<Game> initGames(int numberOfGames) {
    List<Game> games = new ArrayList<>();
    for (int i = 1; i <= numberOfGames; i++) {
      games.add(Game.builder()
          .id((long) i)
          .uuid(UUID + i)
          .drawDeck(new LinkedList<>())
          .playersInGame(new ArrayList<>())
          .actualPlayer(ACTUAL_PLAYER)
          .log(new LinkedList<>())
          .hiddenLog(new LinkedList<>())
          .isGameOver(false)
          .build());
    }
    return games;
  }

  public static List<Player> initPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    for (int i = 1; i <= numberOfPlayers; i++) {
      players.add(Player.builder()
          .id((long) i)
          .uuid(UUID + i)
          .name(PLAYER_NAME + i)
          .cardsInHand(new ArrayList<>())
          .playedCards(new ArrayList<>())
          .numberOfLetters(0)
          .isInPlay(true)
          .orderNumber(null)
          .build());
    }
    return players;
  }

  public void assertGeneratedValuesOfGamesAreEquals(int numberOfPreGeneratedGames, List<GodModeDto> godModeDtoList) {
    for (int i = 1; i <= numberOfPreGeneratedGames ; i++) {
      GodModeDto actualDto = godModeDtoList.get(i - 1);
      assertEquals(i, actualDto.getId());
      assertEquals(UUID + i, actualDto.getUuid());
      assertEquals(ACTUAL_PLAYER, actualDto.getActualPlayer());
    }
  }
}
