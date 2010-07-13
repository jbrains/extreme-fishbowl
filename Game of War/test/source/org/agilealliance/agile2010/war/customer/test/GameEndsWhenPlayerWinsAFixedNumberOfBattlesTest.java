package org.agilealliance.agile2010.war.customer.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest {
	public interface Battle {
		void play();

		Object winner();
	}

	private Object jbrains;
	private Object coreyhaines;

	@Test
	public void playerOneWinsAfterOneBattle() throws Exception {
		startGameWith(jbrains, coreyhaines);

		Battle battle = supposeJbrainsWinsNextBattle();
		battle.play();

		assertSame(jbrains, battle.winner());
	}

	private Battle supposeJbrainsWinsNextBattle() {
		return new Battle() {
			public void play() {
			}

			public Object winner() {
				return jbrains;
			}
		};
	}

	private void startGameWith(Object playerOne, Object playerTwo) {
	}
}
