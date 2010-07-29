package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeckComparesCardsTest {
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
