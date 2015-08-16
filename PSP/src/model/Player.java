package model;

public class Player {
	private int playerNum;
	private char playerParty;
	private int idealPt;
	private int budget;
	private int[][] info;
	private boolean doneWithRound;
	private String round;

	public Player(int playerNum, char playerParty, int idealPt, int budget) {
		super();
		this.playerNum = playerNum;
		this.playerParty = playerParty;
		this.idealPt = idealPt;
		this.budget = budget;
		info = new int[4][2];
		doneWithRound = false;
		round = "straw";
	}

	public int getPlayerNumber() {
		return playerNum;
	}

	public char getParty() {
		return playerParty;
	}

	public int getIdealPt() {
		return idealPt;
	}

	public int getBudget() {
		return budget;
	}

	public void spendBudget(int amount) {
		budget -= amount;
	}

	public int[][] getInfo() {
		return info;
	}

	public void doneWithRound() {
		this.doneWithRound = true;
	}

	public boolean checkIfDone() {
		return this.doneWithRound;
	}

	public void newRound() {
		this.doneWithRound = false;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String newRound) {
		round = newRound;
	}
}
