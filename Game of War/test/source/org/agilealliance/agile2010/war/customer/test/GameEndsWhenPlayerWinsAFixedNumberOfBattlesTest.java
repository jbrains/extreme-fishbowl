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

		private final Configuration configuration;

		public GameOfWarConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}

		public GameOfWar withPlayers(Object... players) {
			return new GameOfWar(configuration, players);
		}

	}

	public interface Configuration {
		int numberOfBattlesToWin();
	}

	public static class GameOfWar {
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
	public void playerOneWinsAfterOneBattle() throws Exception {
		startGameWith(jbrains, coreyhaines, new Configuration() {
			@Override
			public int numberOfBattlesToWin() {
				return 1;
			}
		});

		gameOfWar.signalBattleWinner(jbrains);

		assertSame(jbrains, gameOfWar.winner());
	}

	@Test
	public void playerOneWinsAfterTwoBattles() throws Exception {
		startGameWith(jbrains, coreyhaines, new Configuration() {
			@Override
			public int numberOfBattlesToWin() {
				return 2;
			}
		});

		gameOfWar.signalBattleWinner(jbrains);
		gameOfWar.signalBattleWinner(jbrains);

		assertSame(jbrains, gameOfWar.winner());
	}

	@Test
	public void needingTwoBattlesToWinNobodyWinsAfterBattleOne()
			throws Exception {

		startGameWith(jbrains, coreyhaines, new Configuration() {
			@Override
			public int numberOfBattlesToWin() {
				return 2;
			}
		});

		gameOfWar.signalBattleWinner(jbrains);

		assertNull(gameOfWar.winner());
	}

	@Test
	public void needingFiveBattlesToWin() throws Exception {
		startGameWith(jbrains, coreyhaines, new Configuration() {
			public int numberOfBattlesToWin() {
				return 5;
			}
		});

		supposeNextBattleWinnerIs(jbrains);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(coreyhaines);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(jbrains);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(coreyhaines);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(jbrains);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(coreyhaines);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(jbrains);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(coreyhaines);
		assertNull(gameOfWar.winner());

		supposeNextBattleWinnerIs(coreyhaines);
		assertSame(coreyhaines, gameOfWar.winner());
	}

	private void supposeNextBattleWinnerIs(Object battleWinner) {
		gameOfWar.signalBattleWinner(battleWinner);
	}

	private void startGameWith(Object playerOne, Object playerTwo,
			Configuration configuration) {

		gameOfWar = new GameOfWarConfiguration(configuration).withPlayers(
				playerOne, playerTwo);
	}
}
