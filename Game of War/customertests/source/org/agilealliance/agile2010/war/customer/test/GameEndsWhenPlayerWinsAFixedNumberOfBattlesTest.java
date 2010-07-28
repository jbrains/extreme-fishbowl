package org.agilealliance.agile2010.war.customer.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

public class GameEndsWhenPlayerWinsAFixedNumberOfBattlesTest {
	public static class GameOfWarConfiguration {
		// SMELL No need for separate Configuration interface
		private final Configuration configuration;

		public GameOfWarConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}

		public GameOfWar playWith(Object... players) {
			return new GameOfWar(configuration, players);
		}
	}

	public interface Configuration {
		int numberOfBattlesToWin();
	}

	public static class GameOfWar {
		// SMELL Primitive Obsession: Naked Map of player to battles won
		private final Map<Object, Integer> winsByPlayer;
		private final Configuration configuration;

		public GameOfWar(Configuration configuration, Object[] players) {
			this.configuration = configuration;
			this.winsByPlayer = initializeScoreboard(players);
		}

		// REFACTOR Encapsulate in Scoreboard object
		private static Map<Object, Integer> initializeScoreboard(
				Object[] players) {

			final Map<Object, Integer> winsByPlayer = new HashMap<Object, Integer>();
			CollectionUtils.forAllDo(Arrays.asList(players), new Closure() {
				@Override
				public void execute(Object input) {
					winsByPlayer.put(input, 0);
				}
			});
			return winsByPlayer;
		}

		public Object winner() {
			@SuppressWarnings("unchecked")
			Map.Entry<Object, Integer> winningScoreboardEntry = (Map.Entry<Object, Integer>) CollectionUtils
					.find(winsByPlayer.entrySet(), new Predicate() {
						@Override
						public boolean evaluate(Object input) {
							Map.Entry<Object, Integer> each = (Map.Entry<Object, Integer>) input;
							return each.getValue() >= configuration
									.numberOfBattlesToWin();
						}
					});

			if (winningScoreboardEntry == null)
				return null;

			return winningScoreboardEntry.getKey();
		}

		public void signalBattleWinner(Object battleWinner) {
			winsByPlayer.put(battleWinner, winsByPlayer.get(battleWinner) + 1);
		}
	}

	private Object jbrains = new Object() {
		public String toString() {
			return "jbrains";
		}
	};

	private Object coreyhaines = new Object() {
		public String toString() {
			return "coreyhaines";
		}
	};

	private GameOfWar gameOfWar;

	@Test
	public void needingFiveBattlesToWin() throws Exception {
		playGameWithPlayersToFixedNumberOfWins(jbrains, coreyhaines, 5);
		givePlayerCardsWithRanks(jbrains, 7, 2, 7, 2, 7, 2, 7, 2, 2);
		givePlayerCardsWithRanks(coreyhaines, 2, 7, 2, 7, 2, 7, 2, 7, 7);

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertNull(gameOfWar.winner());

		playNextBattle();
		assertSame(coreyhaines, gameOfWar.winner());
	}

	private void playNextBattle() {
		// TODO Auto-generated method stub
	}

	private void givePlayerCardsWithRanks(Object player, int... ranks) {
	}

	private void playGameWithPlayersToFixedNumberOfWins(Object playerOne,
			Object playerTwo, final int winsNeeded) {

		gameOfWar = new GameOfWarConfiguration(new Configuration() {
			@Override
			public int numberOfBattlesToWin() {
				return winsNeeded;
			}
		}).playWith(playerOne, playerTwo);
	}
}
