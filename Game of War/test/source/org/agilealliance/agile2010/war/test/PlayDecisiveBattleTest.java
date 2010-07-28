package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertSame;

import org.agilealliance.agile2010.war.test.DeckComparesCardsTest.Card;
import org.junit.Test;

public class PlayDecisiveBattleTest {
	public static class PlayerWithCard {
		private final Object player;
		private final Card card;

		public PlayerWithCard(Object player, int cardWithRank) {
			this.player = player;
			this.card = new Card(cardWithRank);
		}

		public Object getPlayer() {
			return player;
		}

		public boolean beats(PlayerWithCard that) {
			return this.card.beats(that.card);
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

		playBattle(spyBattleListener, jbrainsWithWinningCard,
				coreyhainesWithLosingCard);

		assertSame(jbrains, winner);
	}

	@Test
	public void secondPlayerWins() throws Exception {
		PlayerWithCard jbrainsWithLosingCard = playerWithNextCard(jbrains, 3);
		PlayerWithCard coreyhainesWithWinningCard = playerWithNextCard(
				coreyhaines, 4);

		playBattle(spyBattleListener, jbrainsWithLosingCard,
				coreyhainesWithWinningCard);

		assertSame(coreyhaines, winner);
	}

	// SMELL PlayerWithDeck, or Map<Object, Deck>?
	private void playBattle(BattleListener battleListener,
			PlayerWithCard firstPlayerWithDeck,
			PlayerWithCard secondPlayerWithDeck) {

		battleListener.signalBattleWinner(chooseWinningPlayer(
				firstPlayerWithDeck, secondPlayerWithDeck));
	}

	private Object chooseWinningPlayer(PlayerWithCard firstPlayerWithDeck,
			PlayerWithCard secondPlayerWithDeck) {

		// SMELL Looks like max(), but not sure whether I want to do that yet
		if (firstPlayerWithDeck.beats(secondPlayerWithDeck))
			return firstPlayerWithDeck.getPlayer();
		else if (secondPlayerWithDeck.beats(firstPlayerWithDeck))
			return secondPlayerWithDeck.getPlayer();

		return null;
	}

	private PlayerWithCard playerWithNextCard(Object player, int cardWithRank) {
		return new PlayerWithCard(player, cardWithRank);
	}
}
