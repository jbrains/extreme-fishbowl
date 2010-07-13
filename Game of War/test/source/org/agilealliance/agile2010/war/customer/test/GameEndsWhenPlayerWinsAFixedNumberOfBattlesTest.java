package org.agilealliance.agile2010.war.customer.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest {
	public class GameOfWar {
		public void playTurn() {
		}

		public Object winner() {
			return null;
		}
	}

	private Object jbrains = new Object();
	private Object coreyhaines = new Object();
	private GameOfWar gameOfWar;

	@Test
	public void playerOneWinsAfterOneBattle() throws Exception {
		startGameWith(jbrains, coreyhaines);

		supposeJbrainsWinsFirstBattle();
		gameOfWar.playTurn();

		assertSame(jbrains, gameOfWar.winner());
	}

	private void supposeJbrainsWinsFirstBattle() {
		gameOfWar = new GameOfWar() {
			private Object winner = null;
			private int turnsPlayed = 0;

			public void playTurn() {
				if (turnsPlayed == 0)
					winner = jbrains;

				turnsPlayed++;
			}

			public Object winner() {
				return winner;
			};
		};
	}

	@Test
	public void playerOneWinsAfterTwoBattles() throws Exception {
		startGameWith(jbrains, coreyhaines);

		supposeJbrainsWinsSecondBattle();
		gameOfWar.playTurn();
		gameOfWar.playTurn();

		assertSame(jbrains, gameOfWar.winner());
	}

	@Test
	public void needingTwoBattlesToWinNobodyWinsAfterBattleOne()
			throws Exception {
		startGameWith(jbrains, coreyhaines);

		supposeJbrainsWinsSecondBattle();
		gameOfWar.playTurn();

		assertNull(gameOfWar.winner());
	}

	private void supposeJbrainsWinsSecondBattle() {
		gameOfWar = new GameOfWar() {
			private Object winner = null;
			private int turnsPlayed = 0;

			public void playTurn() {
				if (turnsPlayed == 1)
					winner = jbrains;

				turnsPlayed++;
			}

			public Object winner() {
				return winner;
			};
		};
	}

	private void startGameWith(Object playerOne, Object playerTwo) {
	}
}
