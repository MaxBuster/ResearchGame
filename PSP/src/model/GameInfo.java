package model;

import java.util.LinkedList;

public class GameInfo {
	private int gameNum;
	private int[] idealPts;
	private int[] distribution;
	private int budget;
	private LinkedList<Player> players = new LinkedList<Player>();
	
	public GameInfo(int gameNum, int[] idealPts, int[] distribution, int budget) {
		this.gameNum = gameNum;
		this.idealPts = idealPts;
		this.distribution = distribution;
		this.budget = budget;
	}
	
	public int getGameNum() {
		return gameNum;
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
	
	public LinkedList<Player> getPlayers() {
		return players;
	}
}
