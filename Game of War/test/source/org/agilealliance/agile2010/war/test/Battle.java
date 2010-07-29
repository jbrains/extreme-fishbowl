package org.agilealliance.agile2010.war.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.agilealliance.agile2010.war.test.PlayDecisiveBattleTest.BattleListener;

public class Battle {
	// REFACTOR Replace first, second with collection
	public void playBattle(BattleListener battleListener,
			PlayerWithCard firstPlayerWithCard,
			PlayerWithCard secondPlayerWithCard) {

		battleListener.signalBattleWinner(chooseWinningPlayer(
				firstPlayerWithCard, secondPlayerWithCard));
	}

	// REFACTOR Replace first, second with collection
	private Object chooseWinningPlayer(PlayerWithCard firstPlayerWithCard,
			PlayerWithCard secondPlayerWithCard) {

		return chooseWinningPlayerWithCard(firstPlayerWithCard,
				secondPlayerWithCard).getPlayer();
	}

	public PlayerWithCard chooseWinningPlayerWithCard(
			PlayerWithCard... playersWithCard) {

		if (playersWithCard.length == 0)
			return PlayerWithCard.nullObject();

		// We need to operate on a copy because Collections.sort() rearranges
		// the list.
		List<PlayerWithCard> copyOfPlayersWithCard = new ArrayList<PlayerWithCard>(
				Arrays.asList(playersWithCard));

		Collections.sort(copyOfPlayersWithCard,
				new Comparator<PlayerWithCard>() {
					@Override
					public int compare(PlayerWithCard first,
							PlayerWithCard second) {

						// Sort top player to the "front" of the collection
						return first.beats(second) ? -1 : 1;
					}
				});

		return copyOfPlayersWithCard.get(0);
	}
}