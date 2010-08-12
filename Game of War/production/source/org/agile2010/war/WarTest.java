package org.agile2010.war;

import static org.junit.Assert.*;

import org.junit.Test;

public class WarTest {
	
	@Test
	public void fiveBattlesWinsTheGame() throws Exception {
		War war = new War();		
		
		war.battle(Card.Three, Card.Five);
		war.battle(Card.Two, Card.Seven);
		war.battle(Card.Four, Card.Five);
		war.battle(Card.Four, Card.Five);
		war.battle(Card.Four, Card.Five);
		assertTrue(war.isGameOver());
		assertEquals(War.ARMY_TWO, war.getWinner());				
	}
	
	@Test
	public void playerOneCanWinAlso() throws Exception {
		War war = new War();
		war.battle(Card.Ten, Card.Five);
		war.battle(Card.Nine, Card.Seven);
		war.battle(Card.Eight, Card.Five);
		war.battle(Card.Eight, Card.Five);
		war.battle(Card.Ten, Card.Five);
		assertTrue(war.isGameOver());
		assertEquals(War.ARMY_ONE, war.getWinner());				
		
	}
	
	@Test
	public void gameIsNotWonAfterFourBattles() 	{
		War war = new War();
		war.battle(Card.Two, Card.Three);
		war.battle(Card.Two, Card.Three);
		war.battle(Card.Two, Card.Three);
		war.battle(Card.Two, Card.Three);
		assertEquals(false, war.isGameOver());
		assertEquals(War.STALEMATE, war.getWinner());
	}
	
	@Test
	public void faceCardsCanDoBattle()
	{
		War war = new War();
		war.battle(Card.King, Card.Queen);
		war.battle(Card.King, Card.Queen);
		war.battle(Card.King, Card.Queen);
		war.battle(Card.Ace, Card.Jack);
		war.battle(Card.King, Card.Queen);
		assertTrue(war.isGameOver());
		assertEquals(War.ARMY_ONE, war.getWinner());
	}

}
