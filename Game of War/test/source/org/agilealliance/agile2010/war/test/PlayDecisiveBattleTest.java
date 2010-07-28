package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertSame;

import org.agilealliance.agile2010.war.test.DeckComparesCardsTest.Card;
import org.junit.Test;

public class PlayDecisiveBattleTest {
	public static class Battle {
		
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

		static PlayerWithCard nullObject() {
			return new PlayerWithCard(null, 0);
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
		return new PlayerWithCard(player, cardWithRank);
	}
}
