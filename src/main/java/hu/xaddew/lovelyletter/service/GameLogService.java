package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.enums.GameLog;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameLogService {

  private static final String HAS = " kezében ";
  private static final String PLAYED_A_GUARD_AND_THINKS_THAT = " Őrt játszott ki. Szerinte ";
  private static final String WAS = " volt.";
  private static final String IN_HAND = " kézben lévő lapjával.";

  public String addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(Player actualPlayer,
      Card cardWantToPlayOut, Game game) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott lapja egy " + cardWantToPlayOut.getName()
            + " volt, de megcélozható játékos híján nem történt semmi.");
  }

  public String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " eldobta a Hercegnőt, így kiesett a játékból.");
  }

  public String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott egy Királyt, ő és " + targetPlayer.getName()
            + " kártyát cseréltek.");
  }

  public String addLogWhenAPlayerUseChancellorToDrawZeroCard(Player actualPlayer, Game game) {
    return game.addLog(actualPlayer.getName()
        + " kijátszott egy Kancellárt, de mivel a húzópakli üres volt, a kártyának nincsen hatása.");
  }

  public String addLogWhenAPlayerUseChancellorToDrawOneOrTwoCards(Player actualPlayer, Game game,
      int numberOfDrawnCards) {
    return game.addLog(
        actualPlayer.getName() + " kijátszott egy Kancellárt, amivel felhúzta a pakli felső "
            + numberOfDrawnCards + " lapját. A kezében lévő " + (numberOfDrawnCards + 1)
            + " lapból egyet meg kell tartania, a többit pedig visszatennie a pakli aljára.");
  }

  public String addLogWhenAPlayerUseChancellorToReturnCards(Player actualPlayer, int numberOfCards,
      Game game) {
    return game.addLog(actualPlayer.getName() + ", miután Kancellárral húzott " + numberOfCards
        + " lapot, visszatett ugyanennyit a pakliba.");
  }

  public String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer,
      Game game) {
    return game.addLog(
        actualPlayer.getName() + " Herceggel eldobta a Hercegnőt, így kiesett a játékból.");
  }

  public String addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(Player actualPlayer,
      Card cardToDiscard, Game game) {
    return game.addLog(
        actualPlayer.getName() + " Herceggel eldobta a saját kézben lévő lapját, ami egy "
            + cardToDiscard.getName() + WAS);
  }

  public String addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(Player actualPlayer,
      Player targetPlayer, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy Hercegnő volt, így " + targetPlayer.getName() + " kiesett a játékból.");
  }

  public String addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Card cardToDiscard, Game game) {
    return game.addLog(actualPlayer.getName() + " Herceggel eldobatta " + targetPlayer.getName()
        + " lapját, ami egy " + cardToDiscard.getName() + WAS);
  }

  public String addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpyOrKili(Player actualPlayer,
      String cardName, Game game) {
    return game.addLog(actualPlayer.getName() + " kijátszott lapja egy " + cardName + WAS);
  }

  public String addLogWhenAPlayerShouldDiscardKiliByBaron(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer.getName()
            + IN_HAND + " " + targetPlayer.getName()
            + " kézben lévő lapja " + cardToDiscard.getName()
            + " volt, aki megmentett gazdáját a kiesétől ("
            + targetPlayer.getName() + " húzott egy új lapot).");
  }

  public String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer.getName()
            + IN_HAND + " " + targetPlayer.getName()
            + " kiesett a játékból, kézben lévő lapját ("
            + cardToDiscard.getName() + ") pedig eldobta.");
  }

  public String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer
            .getName()
            + IN_HAND + " " + actualPlayer.getName()
            + " kiesett a játékból, kézben lévő lapját ("
            + cardToDiscard.getName() + ") pedig eldobta.");
  }

  public String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer,
      Game game) {
    return game.addLog(
        actualPlayer.getName() + GameLog.WITH_BARON_COMPARE_CARD + targetPlayer
            .getName()
            + " kézben lévő lapjával. A lapok értéke azonos volt, így senki sem esett ki a játékból.");
  }

  public String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game) {
    return game.addLog(
        actualPlayer.getName() + " megnézte, mi van " + targetPlayer.getName() + " kezében.");
  }

  public String addLogWhenAPlayerShouldDiscardKiliByGuard(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + HAS + namedCard + " van. Így igaz. "
            + targetPlayer.getName() + " eldobta a lapját és ahelyett, hogy kiesett volna,"
            + " húzott egy új lapot.");
  }

  public String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + HAS + namedCard + " van. Így igaz. "
            + targetPlayer.getName() + " kiesett a játékból.");
  }

  public String addLogWhenAPlayerUseGuardUnsuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game) {
    String namedCard = requestDto.getAdditionalInfo().getNamedCard();
    return game.addLog(
        actualPlayer.getName() + PLAYED_A_GUARD_AND_THINKS_THAT + targetPlayer.getName()
            + HAS + namedCard + " van. Nem talált.");
  }
}
