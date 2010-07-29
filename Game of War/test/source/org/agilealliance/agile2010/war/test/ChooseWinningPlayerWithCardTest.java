package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ChooseWinningPlayerWithCardTest {
	@Test
	public void firstPlayerWins() throws Exception {
		Object firstPlayer = new Object();
		Object secondPlayer = new Object();

		PlayerWithCard firstPlayerWithCard = new PlayerWithCard(firstPlayer,
				new Card(7));
		PlayerWithCard secondPlayerWithCard = new PlayerWithCard(secondPlayer,
				new Card(6));

		assertSame(firstPlayerWithCard, new Battle().chooseWinningPlayerWithCard(
				firstPlayerWithCard, secondPlayerWithCard));
	}
	
	@Test
	public void secondPlayerWins() throws Exception {
		Object firstPlayer = new Object();
		Object secondPlayer = new Object();
		
		PlayerWithCard firstPlayerWithCard = new PlayerWithCard(firstPlayer,
				new Card(2));
		PlayerWithCard secondPlayerWithCard = new PlayerWithCard(secondPlayer,
				new Card(3));
		
		assertSame(secondPlayerWithCard, new Battle().chooseWinningPlayerWithCard(
				firstPlayerWithCard, secondPlayerWithCard));
	}

	@Test
	public void noPlayers() throws Exception {
		assertSame(PlayerWithCard.nullObject(), new Battle().chooseWinningPlayerWithCard());
	}
}
