package org.agilealliance.agile2010.war.test;

import org.agilealliance.agile2010.war.test.PlayDecisiveBattleTest.BattleListener;

public class Battle {
	
	public void playBattle(BattleListener battleListener,
			PlayerWithCard firstPlayerWithCard,
			PlayerWithCard secondPlayerWithCard) {

		battleListener.signalBattleWinner(chooseWinningPlayer(
				firstPlayerWithCard, secondPlayerWithCard));
	}

	private Object chooseWinningPlayer(PlayerWithCard firstPlayerWithCard,
			PlayerWithCard secondPlayerWithCard) {

		return chooseWinningPlayerWithCard(firstPlayerWithCard,
				secondPlayerWithCard).getPlayer();
	}

	private PlayerWithCard chooseWinningPlayerWithCard(
			PlayerWithCard firstPlayerWithCard,
			PlayerWithCard secondPlayerWithCard) {

		// REFACTOR sort(playersWithCards).first()
		if (firstPlayerWithCard.beats(secondPlayerWithCard))
			return firstPlayerWithCard;
		else if (secondPlayerWithCard.beats(firstPlayerWithCard))
			return secondPlayerWithCard;

		return PlayerWithCard.nullObject();
	}
}