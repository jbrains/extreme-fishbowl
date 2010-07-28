package org.agilealliance.agile2010.war.test;

public class PlayerWithCard {
	private final Object player;
	private final Card card;

	public PlayerWithCard(Object player, Card card) {
		this.player = player;
		this.card = card;
	}

	public Object getPlayer() {
		return player;
	}

	public boolean beats(PlayerWithCard that) {
		return this.card.beats(that.card);
	}

	public static PlayerWithCard nullObject() {
		return new PlayerWithCard(null, null);
	}
}