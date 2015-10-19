package model;

public class GameInfo {
	private int[] idealPts;
	private int[] distribution;
	private int budget;
	
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
}
