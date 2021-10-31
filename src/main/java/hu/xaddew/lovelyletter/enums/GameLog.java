package hu.xaddew.lovelyletter.enums;

public enum GameLog {

  ACTUAL_PLAYER_IS("Soron lévő játékos: "),
  GAME_IS_CREATED_UUID("Játék létrehozva. Uuid: "),
  PLAYERS_ARE("Játékosok: "),
  COMPARE_CARD_IN_HAND_WITH(" Báróval összehasonlította a kézben lévő lapját "),
  ROUND_IS_OVER_ONLY_ONE_PLAYER_LEFT("A forduló véget ért, mert csak egy játékos maradt bent."),
  ROUND_IS_OVER_DRAW_DECK_IS_EMPTY("A forduló véget ért, mert elfogyott a húzópakli."),
  WON_THE_ROUND(" nyerte a fordulót!"),
  GAME_IS_OVER_STATUS_MESSAGE(
      "A játék véget ért, mivel valaki elég szerelmes levelet gyűjtött össze!"),
  NEW_ROUND_BEGINS_STATUS_MESSAGE("Új forduló kezdődik. "),
  CONGRATULATE("Gratulálunk, "),
  GAME_IS_CLOSED_DUE_TO_INACTIVITY("Inaktivitás miatt a játék lezárásra került ekkor: "),
  PLAYER_CHOOSES_FROM_THE_CARDS_DRAWN_BY_CHANCELLOR(
      ", aki éppen a Kancellár által húzott lapokból választ..."),
  CARDS_DRAWN_BY_CHANCELLOR("A Kancellárral húzott lap(ok): ");

  private final String logText;

  GameLog(String log) {
    this.logText = log;
  }

  @Override
  public String toString() {
    return logText;
  }
}
