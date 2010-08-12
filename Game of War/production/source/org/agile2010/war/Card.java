package org.agile2010.war;

public enum Card {
	Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten,
	Jack, Queen, King, Ace;

	public boolean beats(Card otherCard) {
		return this.compareTo(otherCard) > 0;
	}
	

}
