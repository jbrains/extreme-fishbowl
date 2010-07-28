package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertSame;

import org.agilealliance.agile2010.war.test.DeckComparesCardsTest.Card;
import org.junit.Test;

public class PlayDecisiveBattleTest {
	public static class PlayerWithDeck {
		private final int cardWithRank;

		public PlayerWithDeck(Object player, int cardWithRank) {
			this.cardWithRank = cardWithRank;
		}

		public Card nextCard() {
			return new Card(cardWithRank);
		}
	}

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

	@Test
	public void firstPlayerWins() throws Exception {
		PlayerWithDeck jbrainsWithWinningCard = playerWithNextCard(jbrains, 7);
		PlayerWithDeck coreyhainesWithLosingCard = playerWithNextCard(
				coreyhaines, 2);

		playBattle(new BattleListener() {
			public void signalBattleWinner(Object battleWinner) {
				PlayDecisiveBattleTest.this.winner = battleWinner;
			}
		}, jbrainsWithWinningCard, coreyhainesWithLosingCard);

		assertSame(jbrains, winner);
	}

	@Test
	public void secondPlayerWins() throws Exception {
		PlayerWithDeck jbrainsWithLosingCard = playerWithNextCard(jbrains, 3);
		PlayerWithDeck coreyhainesWithWinningCard = playerWithNextCard(
				coreyhaines, 4);

		playBattle(new BattleListener() {
			public void signalBattleWinner(Object battleWinner) {
				PlayDecisiveBattleTest.this.winner = battleWinner;
			}
		}, jbrainsWithLosingCard, coreyhainesWithWinningCard);

		assertSame(coreyhaines, winner);
	}

	private void playBattle(BattleListener battleListener,
			PlayerWithDeck firstPlayer, PlayerWithDeck secondPlayer) {

		if (firstPlayer.nextCard().beats(secondPlayer.nextCard()))
			battleListener.signalBattleWinner(jbrains);
		else if (secondPlayer.nextCard().beats(firstPlayer.nextCard()))
			battleListener.signalBattleWinner(coreyhaines);
	}

	private PlayerWithDeck playerWithNextCard(Object player, int cardWithRank) {
		return new PlayerWithDeck(player, cardWithRank);
	}
}
