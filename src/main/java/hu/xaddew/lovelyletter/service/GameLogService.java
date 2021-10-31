package hu.xaddew.lovelyletter.service;

import hu.xaddew.lovelyletter.dto.PlayCardRequestDto;
import hu.xaddew.lovelyletter.model.Card;
import hu.xaddew.lovelyletter.model.Game;
import hu.xaddew.lovelyletter.model.Player;

public interface GameLogService {

  String addLogWhenAPlayerUseKingOrBaronOrPriestOrGuardWithoutEffect(Player actualPlayer,
      Card cardWantToPlayOut, Game game);

  String addLogWhenAPlayerMustDiscardPrincess(Player actualPlayer, Game game);

  String addLogWhenAPlayerUseKing(Player actualPlayer, Player targetPlayer, Game game);

  String addLogWhenAPlayerUseChancellorToDrawZeroCard(Player actualPlayer, Game game);

  String addLogWhenAPlayerUseChancellorToDrawOneOrTwoCards(Player actualPlayer, Game game,
      int numberOfDrawnCards);

  String addLogWhenAPlayerUseChancellorToReturnCards(Player actualPlayer, int numberOfCards, Game game);

  String addLogIfAPlayerMustDiscardPrincessBecauseOfHerOrHisOwnPrince(Player actualPlayer,
      Game game);

  String addLogWhenAPlayerUsePrinceToDiscardHerOrHisOwnCard(Player actualPlayer,
      Card cardToDiscard, Game game);

  String addLogIfAPlayerMustDiscardPrincessBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Game game);

  String addLogIfAPlayerMustDiscardHisOrHerCardBecauseOfAnotherPlayersPrince(
      Player actualPlayer, Player targetPlayer, Card cardToDiscard, Game game);

  String addLogWhenAPlayerPlaysOutCountessOrHandmaidOrSpyOrKili(Player actualPlayer,
      String cardName, Game game);

  String addLogWhenAPlayerShouldDiscardKiliByBaron(Player targetPlayer, Card cardToDiscard,
      Player actualPlayer, Game game);

  String addLogWhenAPlayerUseBaronSuccessful(Player targetPlayer,
      Card cardToDiscard, Player actualPlayer, Game game);

  String addLogWhenAPlayerUseBaronUnsuccessful(Player targetPlayer,
      Card cardToDiscard, Player actualPlayer, Game game);

  String addLogWhenAPlayerUseBaronAndItsDraw(Player targetPlayer, Player actualPlayer,
      Game game);

  String addLogWhenAPlayerUsePriest(Player actualPlayer, Player targetPlayer, Game game);

  String addLogWhenAPlayerShouldDiscardKiliByGuard(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game);

  String addLogWhenAPlayerUseGuardSuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game);

  String addLogWhenAPlayerUseGuardUnsuccessfully(PlayCardRequestDto requestDto,
      Player actualPlayer, Player targetPlayer, Game game);


}
