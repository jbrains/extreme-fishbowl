package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PopCardsFromDeckTest {
	public static class Deck {
		public static Deck withCardsWithRanks(int[] ranks) {
			return new Deck();
		}

		public Card popCard() {
			return null;
		}
	}

	@Test
	public void empty() throws Exception {
		assertNull(Deck.withCardsWithRanks(new int[] {}).popCard());
	}
}
