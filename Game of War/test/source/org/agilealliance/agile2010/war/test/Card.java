package org.agilealliance.agile2010.war.test;

public class Card implements Comparable<Card> {
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