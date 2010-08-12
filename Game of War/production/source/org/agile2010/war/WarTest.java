package org.agile2010.war;

import static org.junit.Assert.*;

import org.junit.Test;

public class WarTest {
	
	@Test
	public void fiveBattlesWinsTheGame() throws Exception {
		War war = new War();		
		
		war.battle(3, 5);
		war.battle(2, 7);
		war.battle(4, 5);
		war.battle(4, 5);
		war.battle(4, 5);
		assertTrue(war.isGameOver());
		assertEquals(War.ARMY_TWO, war.getWinner());				
	}
	
	@Test
	public void playerOneCanWinAlso() throws Exception {
		War war = new War();
		war.battle(10, 5);
		war.battle(9, 7);
		war.battle(8, 5);
		war.battle(8, 5);
		war.battle(10, 5);
		assertTrue(war.isGameOver());
		assertEquals(War.ARMY_ONE, war.getWinner());				
		
	}
	
	@Test
	public void gameIsNotWonAfter4Battles() 	{
		War war = new War();
		war.battle(1, 2);
		war.battle(1, 2);
		war.battle(1, 2);
		war.battle(1, 2);
		assertEquals(false, war.isGameOver());
		assertEquals(War.STALEMATE, war.getWinner());
	}
	
//	@Test
//	public void faceCardsCanDoBattle()
//	{
//		War war = new War();
//		war.battle("K", "Q");
//		war.battle("K", "Q");
//		war.battle("K", "Q");
//		war.battle("K", "Q");
//		war.battle("K", "Q");
//		assertTrue(war.isGameOver());
//		assertEquals(War.ARMY_ONE, war.getWinner());
//	}

}
