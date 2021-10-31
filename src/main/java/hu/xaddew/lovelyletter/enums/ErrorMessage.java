package hu.xaddew.lovelyletter.enums;

public enum ErrorMessage {

  MISSING_GAME_CREATE_REQUEST_ERROR_MESSAGE("Hibás kérés játék létrehozásakor."),
  PLAYER_NUMBER_IN_CLASSIC_GAME_ERROR_MESSAGE("A játékosok száma 2, 3 vagy 4 lehet."),
  PLAYER_NUMBER_IN_2019_VERSION_GAME_ERROR_MESSAGE(
      "A 2019-es kiadású játékban a játékosok száma 2-6 között lehet."),
  PLAYER_NAME_ERROR_MESSAGE("Nem szerepelhet két játékos ugyanazzal a névvel!"),
  RESERVED_NAMES_ERROR_MESSAGE("A játékos neve nem lehet az alábbiak valamelyike: "),
  INVALID_CUSTOM_CARD_ERROR_MESSAGE(
      "Egy vagy több ismeretlen kártya szerepel a megadott egyedi kártyák listájában: "),
  PLAYER_PROTECTED_BY_HANDMAID_ERROR_MESSAGE("Az általad választott játékost Szobalány védi."),
  PLAYER_SELF_TARGETING_ERROR_MESSAGE(
      "Király, Báró, Pap és Őr kijátszásakor nem választhatod saját magadat."),
  PLAYER_NOT_FOUND_ERROR_MESSAGE("Nem találtam az általad választott játékost."),
  PLAYER_NOT_SELECTED_ERROR_MESSAGE("Nem választottál másik játékost a kártya hatásához."),
  HAVE_NO_CARD_WHAT_WANT_TO_PLAY_OUT_ERROR_MESSAGE(
      "Nincsen nálad a kártya, amit ki szeretnél játszani."),
  COUNTESS_WITH_KING_OR_PRINCE_ERROR_MESSAGE(
      "Ha a Grófnő a Királlyal vagy a Herceggel egyszerre van a kezedben, a Grófnőt kell eldobnod."),
  NOT_YOUR_TURN_ERROR_MESSAGE("Nem a te köröd van, "),
  NO_GAME_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE("Nem találtam játékot ezzel az uuid-val: "),
  NO_GAME_FOUND_WITH_GIVEN_PLAYER_ERROR_MESSAGE("Nem találtam játékot ezzel a játékossal."),
  PUT_BACK_CARDS_GENERAL_ERROR_MESSAGE("Hiba történt a kártyák visszatételekor."),
  HAVE_NO_CARDS_WHAT_WANT_TO_PUT_BACK_ERROR_MESSAGE(
      "Az általad visszatenni kívánt lap(ok) nincs(enek) nálad."),
  MISSING_PUT_BACK_A_CARD_REQUEST_ERROR_MESSAGE(
      "A visszarakandó kártyák nevének megadása szükséges."),
  PLAYER_IS_ALREADY_OUT_OF_ROUND_ERROR_MESSAGE(
      "Az általad választott játékos már kiesett a fordulóból."),
  GUARD_IS_NOT_TARGETED_WITH_GUARD_ERROR_MESSAGE("Őrrel nem tippelhetsz Őrt."),
  NO_PLAYER_FOUND_WITH_GIVEN_UUID_ERROR_MESSAGE("Nem találtam játékost ezzel az uuid-val: "),
  PLAYER_ORDER_ERROR_MESSAGE("A játékossorrend rosszul került beállításra."),
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
