package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class PlayDecisiveBattleTest {
	public static interface BattleListener {
		void signalBattleWinner(Object battleWinner);
	}

	private Object jbrains = new Object() {
		public String toString() {
			return "jbrains";
		}
	};
	private Object coreyhaines = new Object() {
		public String toString() {
			return "coreyhaines";
		}
	};

	private Object winner;
	private BattleListener spyBattleListener = new BattleListener() {
		public void signalBattleWinner(Object battleWinner) {
			PlayDecisiveBattleTest.this.winner = battleWinner;
		}
	};

	@Test
	public void firstPlayerWins() throws Exception {
		PlayerWithCard jbrainsWithWinningCard = playerWithNextCard(jbrains, 7);
		PlayerWithCard coreyhainesWithLosingCard = playerWithNextCard(
				coreyhaines, 2);

		new Battle().playBattle(spyBattleListener, jbrainsWithWinningCard,
				coreyhainesWithLosingCard);

		assertSame(jbrains, winner);
	}

	@Test
	public void secondPlayerWins() throws Exception {
		PlayerWithCard jbrainsWithLosingCard = playerWithNextCard(jbrains, 3);
		PlayerWithCard coreyhainesWithWinningCard = playerWithNextCard(
				coreyhaines, 4);

		new Battle().playBattle(spyBattleListener, jbrainsWithLosingCard,
				coreyhainesWithWinningCard);

		assertSame(coreyhaines, winner);
	}

	private PlayerWithCard playerWithNextCard(Object player, int cardWithRank) {
		return new PlayerWithCard(player, new Card(cardWithRank));
	}
}
