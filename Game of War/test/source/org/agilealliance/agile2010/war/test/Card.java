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

	@Override
	public boolean equals(Object other) {
		if (other instanceof Card) {
			Card that = (Card) other;
			return this.rank == that.rank;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return rank;
	}
	
	@Override
	public String toString() {
		return String.format("a Card with rank %d", rank);
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