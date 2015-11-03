package model;

import java.util.LinkedList;

public class GameInfo {
	private int[] idealPts;
	private int[] distribution;
	private int budget;
	// FIXME allow add players and candidates so this can be used to save game data
	private LinkedList<Player> players = new LinkedList<Player>();
	
	public GameInfo(int[] idealPts, int[] distribution, int budget) {
		this.idealPts = idealPts;
		this.distribution = distribution;
		this.budget = budget;
	}

	public int[] getIdealPts() {
		return idealPts;
	}

	public int[] getDistribution() {
		return distribution;
	}

	public int getBudget() {
		return budget;
	}
	
	public synchronized void addPlayer(Player player) {
		players.add(player);
	}
}
