package hu.xaddew.lovelyletter.enums;

public enum GameLog {

  ACTUAL_PLAYER_IS("Actual player is: "),
  GAME_IS_CREATED_UUID("Game has created. Uuid: "),
  PLAYERS_ARE("Players are: "),
  WITH_BARON_COMPARE_CARD(" has played a Baron and has compared the card in his/her hand with the card at "),
  ROUND_IS_OVER_ONLY_ONE_PLAYER_HAS_LEFT("Round is over because only one player has left."),
  ROUND_IS_OVER_DRAW_DECK_IS_EMPTY("Round is over because draw deck is empty."),
  WON_THE_ROUND("won the round!"),
  GAME_IS_OVER_STATUS_MESSAGE("The game is over because someone has collected enough love letters!"),
  NEW_ROUND_BEGINS_STATUS_MESSAGE("New round begins. "),
  CONGRATULATE("Congratulate, "),
  GAME_IS_CLOSED_DUE_TO_INACTIVITY("Due to inactivity, the game was closed at: "),
  PLAYER_CHOOSES_FROM_THE_CARDS_DRAWN_BY_CHANCELLOR(
      ", who chooses from the cards drawn by the Chancellor..."),
  CARDS_DRAWN_BY_CHANCELLOR("The card(s) drawn with the Chancellor: ");

  private final String logText;

  GameLog(String logText) {
    this.logText = logText;
  }

  @Override
  public String toString() {
    return logText;
  }
}
