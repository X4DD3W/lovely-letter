package hu.xaddew.lovelyletter.enums;

public enum ErrorMessage {

  MISSING_GAME_CREATE_REQUEST_ERROR_MESSAGE("Bad request when creating game."),
  PLAYER_NUMBER_IN_CLASSIC_GAME_ERROR_MESSAGE("The number of players can be 2, 3 or 4."),
  PLAYER_NUMBER_IN_2019_VERSION_GAME_ERROR_MESSAGE(
      "In the 2019 edition of the game, the number of players can be between 2-6."),
  PLAYER_NAME_ERROR_MESSAGE("Two players cannot appear with the same name!"),
  RESERVED_NAMES_ERROR_MESSAGE("The player's name cannot be one of the following: "),
  INVALID_CUSTOM_CARD_ERROR_MESSAGE(
      "One or more unknown cards appear in the list of specified unique cards: "),
  PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE("The player you choose is protected by a Handmaid."),
  PLAYER_SELF_TARGETING_ERROR_MESSAGE(
      "When playing King, Baron, Priest and Guard, you cannot choose yourself."),
  PLAYER_NOT_FOUND_ERROR_MESSAGE("I did not find the selected player."),
  PLAYER_NOT_SELECTED_ERROR_MESSAGE("You did not choose another player for this card's effect."),
  HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE("You don't have the card you want to play."),
  COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE(
      "If the Countess is in your hand with the King or the Prince at the same time, you must discard the Countess."),
  NOT_YOUR_TURN_ERROR_MESSAGE("It's not your turn, "),
  NO_GAME_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE("No game found with the given uuid: "),
  NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE("No game found with the given player."),
  PUT_BACK_CARDS_GENERAL_ERROR_MESSAGE("An error occurred while returning the cards."),
  HAVE_NO_CARDS_WHAT_WANT_TO_PUT_BACK_ERROR_MESSAGE(
      "You don't have the card(s) you want to return into the draw deck."),
  MISSING_PUT_BACK_A_CARD_REQUEST_ERROR_MESSAGE(
      "It is necessary to enter the name of the cards to be put back."),
  PLAYER_IS_ALREADY_OUT_OF_ROUND_ERROR_MESSAGE(
      "The player you selected has already been out of the round."),
  GUARD_IS_NOT_TARGETED_WITH_GUARD_ERROR_MESSAGE("You can't guess Guard with a Guard."),
  NO_PLAYER_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE("No player found with the given uuid: "),
  PLAYER_ORDER_ERROR_MESSAGE("The player order was set incorrectly."),
  PLAYED_CARD_IS_NOT_PREDEFINED_ONES_ERROR_MESSAGE("Played card is none of the predefined ones.");

  private final String name;

  ErrorMessage(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
