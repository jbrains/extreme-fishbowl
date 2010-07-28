package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.agilealliance.agile2010.war.customer.test.GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest.Configuration;
import org.agilealliance.agile2010.war.customer.test.GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest.GameOfWar;
import org.agilealliance.agile2010.war.customer.test.GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest.GameOfWarConfiguration;
import org.junit.Test;


public class GameEndsWhenPlayerWinsEnoughBattlesTest {

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
	private GameOfWar gameOfWar;

	@Test
	public void playerOneWinsAfterOneBattle() throws Exception {
		playGameWithPlayersToFixedNumberOfWins(jbrains, coreyhaines, 1);
	
		gameOfWar.signalBattleWinner(jbrains);
	
		assertSame(jbrains, gameOfWar.winner());
	}

	@Test
	public void playerOneWinsAfterTwoBattles() throws Exception {
		playGameWithPlayersToFixedNumberOfWins(jbrains, coreyhaines, 2);
	
		gameOfWar.signalBattleWinner(jbrains);
		gameOfWar.signalBattleWinner(jbrains);
	
		assertSame(jbrains, gameOfWar.winner());
	}

	@Test
	public void needingTwoBattlesToWinNobodyWinsAfterBattleOne()
			throws Exception {
	
		playGameWithPlayersToFixedNumberOfWins(jbrains, coreyhaines, 2);
	
		gameOfWar.signalBattleWinner(jbrains);
	
		assertNull(gameOfWar.winner());
	}

	private void playGameWithPlayersToFixedNumberOfWins(Object playerOne,
			Object playerTwo, final int winsNeeded) {
	
		gameOfWar = new GameOfWarConfiguration(new Configuration() {
			@Override
			public int numberOfBattlesToWin() {
				return winsNeeded;
			}
		}).playWith(playerOne, playerTwo);
	}

}
