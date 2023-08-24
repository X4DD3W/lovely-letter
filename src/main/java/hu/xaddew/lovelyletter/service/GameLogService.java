package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.enums.GameLog;
import hu.xaddew.lovelyletter.domain.Card;
import hu.xaddew.lovelyletter.domain.Game;
import hu.xaddew.lovelyletter.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameLogService {

  private static final String PLAYED_A_GUARD_AND_THINKS_THAT = " played a Guard. He/she thinks, that ";

  public String addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(Player actualPlayer,
      Card cardWantToPlayOut, Game game) {
    return game.addLog(
        actualPlayer.getName() + "'s played card was a " + cardWantToPlayOut.getName()
            + " but for lack of a player to target, nothing happened.");
  }

  public String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " has discard the Princess and is out of the round.");
  }

  public String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " played a King, swapped card with " + targetPlayer.getName());
  }

  public String addLogWhenAPlayerUseChancellorToDrawZeroCard(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName()
        + " played a Chancellor, but the draw deck is empty, so nothing happened (card has no effect).");
  }

  public String addLogWhenAPlayerUseChancellorToDrawOneOrTwoCards(Player actualPlayer, Game game,
      int numberOfDrawnCards) {
    return game.addLog(
        actualPlayer.getName() + " played a Chancellor and draw the first " + numberOfDrawnCards
            + " card(s) from the draw deck. He/she must choose one card from the " + (
            numberOfDrawnCards + 1)
            + " cards in his/her hands and the rest must put back to the bottom of the draw deck.");
  }

  public String addLogWhenAPlayerUseChancellorToReturnCards(Player actualPlayer, int numberOfCards,
      Game game) {
    return game.addLog(actualPlayer.getName() + ", after he/she draw " + numberOfCards
        + " cards with Chancellor, put back same number of cards to the bottom of the draw deck.");
  }

  public String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer,
      Game game) {
    return game.addLog(actualPlayer.getName()
        + " has discard Princess because of his/her own Prince and out of the round.");
  }

  public String addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(Player actualPlayer,
      Card cardToDiscard, Game game) {
    return game.addLog(
        actualPlayer.getName() + " has discard his/her other card (" + cardToDiscard.getName()
            + ") with a Prince.");
  }

  public String addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(Player actualPlayer,
      Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " used a Prince and " + targetPlayer.getName()
        + " had to discard his/her card, which was a Princess, so " + targetPlayer.getName()
        + "is out of the round.");
  }

  public String addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Card cardToDiscard, Game game) {
    return game.addLog(actualPlayer.getName() + " used a Prince and " + targetPlayer.getName()
        + " had to discard his/her card, which was a " + cardToDiscard.getName());
  }

  public String addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpyOrKili(Player actualPlayer,
      String cardName, Game game) {
    return game.addLog(actualPlayer.getName() + " played a " + cardName);
  }

  public String addLogWhenAPlayerShouldDiscardKiliByBaron(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer.getName()
            + ". " + targetPlayer.getName()
            + "'s card was " + cardToDiscard.getName()
            + ", who saved his owner from being out of the round ("
            + targetPlayer.getName() + " has draw a new card).");
  }

  public String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer.getName()
            + ". " + targetPlayer.getName() + " is out of the round, discard his/her card ("
            + cardToDiscard.getName() + ").");
  }

  public String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer
            .getName() + ". " + actualPlayer.getName()
            + " is out of the round, discard his/her card (" + cardToDiscard.getName() + ").");
  }

  public String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer,
      Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer.getName()
            + ". Value of the card are the same, no one is out of the round.");
  }

  public String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " looked at the card in " + targetPlayer.getName() + "'s hand.");
  }

  public String addLogWhenAPlayerShouldDiscardKiliByGuard(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + " has a " + namedCard + ". The guess is correct. "
            + targetPlayer.getName() + " has discard his/her card and has draw a new one.");
  }

  public String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + " has a " + namedCard + ". The guess is correct. "
            + targetPlayer.getName() + " is out of the round.");
  }

  public String addLogWhenAPlayerUseGuardUnsuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + " has a " + namedCard + ". Incorrect.");
  }
}
