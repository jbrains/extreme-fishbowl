package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.*;

import org.agilealliance.agile2010.war.test.DeckComparesCardsTest.Card;
import org.junit.Test;

public class DeckComparesCardsTest {
	// REFACTOR Convert to Enum?
	public static class Card implements Comparable<Card> {
		private final int rank;

		public Card(int rank) {
			this.rank = rank;
		}

		@Override
		public int compareTo(Card that) {

			if (rank < that.rank) {
				return -1;
			} else if (rank > that.rank) {
				return 1;
			} else {
				return 0;
			}
		}

		// SMELL Do we want jack() and queen(), or
		// is that only for tests?
		public static Card ace() {
			return new Card(14); // because ace is high (in this game... smell?)
		}

		public static Card king() {
			return new Card(13);
		}

		// SMELL No tests for beats()
		public boolean beats(Card that) {
			return this.compareTo(that) > 0;
		}

	}

	@Test
	public void cardComparedToItselfIsEqual() throws Exception {
		Card card = anyOldCard();
		assertEquals(0, card.compareTo(card));
	}

	@Test
	public void cardComparedToEqualRankIsEqual() throws Exception {
		Card firstCard = new Card(2);
		Card secondCard = new Card(2);
		assertEquals(0, firstCard.compareTo(secondCard));
	}

	@Test
	public void cardIsLowerThanHigherCard() throws Exception {
		Card lower = new Card(2);
		Card higher = new Card(3);
		assertEquals(-1, lower.compareTo(higher));
	}

	@Test
	public void cardIsHigherThanLowerCard() throws Exception {
		Card higher = new Card(7);
		Card lower = new Card(4);
		assertEquals(1, higher.compareTo(lower));
	}

	@Test
	public void aceBeatsKing() throws Exception {
		assertEquals(1, Card.ace().compareTo(Card.king()));
	}

	private Card anyOldCard() {
		return new Card(4);
	}
}