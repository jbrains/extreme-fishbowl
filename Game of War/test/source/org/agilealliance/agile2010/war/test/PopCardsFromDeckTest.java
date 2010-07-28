package org.agilealliance.agile2010.war.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;
import org.junit.Test;

public class PopCardsFromDeckTest {
	public static class Deck {
		private final Buffer cards;

		public Deck(Buffer cards) {
			this.cards = cards;
		}

		public static Deck withCardsWithRanks(Integer[] ranks) {
			return withCards(ranksAsCards(ranks));
		}

		private static Deck withCards(Collection<Card> cards) {
			return new Deck(bufferFor(cards));
		}

		private static BoundedFifoBuffer bufferFor(Collection<Card> cards) {
			// REFACTOR Move to EmptyTolerantBuffer?
			if (cards.isEmpty())
				return new BoundedFifoBuffer(1);
			else
				return new BoundedFifoBuffer(cards);
		}

		@SuppressWarnings("unchecked")
		private static Collection<Card> ranksAsCards(Integer[] ranks) {
			return CollectionUtils.collect(Arrays.asList(ranks),
					new Transformer() {
						@Override
						public Object transform(Object input) {
							Integer rankAsInteger = (Integer) input;
							return new Card(rankAsInteger);
						}
					});
		}

		public Card popCard() {
			// REFACTOR Move to EmptyTolerantBuffer?
			if (cards.isEmpty())
				return null;

			return (Card) cards.remove();
		}
	}

	@Test
	public void empty() throws Exception {
		assertNull(Deck.withCardsWithRanks(new Integer[] {}).popCard());
	}

	@Test
	public void singleCard() throws Exception {
		assertEquals(new Card(2), Deck.withCardsWithRanks(new Integer[] { 2 })
				.popCard());
	}

	@Test
	public void manyCards() throws Exception {
		Deck deck = Deck.withCardsWithRanks(new Integer[] { 2, 3, 4, 5 });

		deck.popCard();
		deck.popCard();
		assertEquals(new Card(4), deck.popCard());
	}
	
	@Test
	public void noMoreCards() throws Exception {
		Deck deck = Deck.withCardsWithRanks(new Integer[] { 2, 3 });
		
		deck.popCard();
		deck.popCard();
		assertNull(deck.popCard());
	}

}
