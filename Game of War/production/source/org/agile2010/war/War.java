package org.agile2010.war;

public class War {

	public static final int ARMY_ONE = 1;
	public static final int ARMY_TWO = 2;
	public static final int STALEMATE = 3; 
	
	private int armyOneVictories;
	private int armyTwoVictories;

	public void battle(int card1, int card2) {
		if (card1 > card2) {
			armyOneVictories++;
		}
		else {
			armyTwoVictories++;
		}
	}

	public int getWinner() {
		if (armyOneVictories < 5 && armyTwoVictories < 5)
			return STALEMATE;
		
		if(armyOneVictories > armyTwoVictories)
			return ARMY_ONE;
		
		return ARMY_TWO;
	}

	public boolean isGameOver() {
		if (armyOneVictories >= 5 || armyTwoVictories >= 5)
			return true;
		return false;
	}

}
