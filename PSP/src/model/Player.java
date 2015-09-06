package model;

public class Player {
	private int playerNum;
	private char playerParty;
	private int idealPt;
	private int budget;
	
	private int[][] infoPurchased;
	private int[][] votes;
	
	private boolean doneWithRound;
	private String round;

	public Player(int playerNum, char playerParty, int idealPt, int budget, int numCandidates) {
		super();
		this.playerNum = playerNum;
		this.playerParty = playerParty;
		this.idealPt = idealPt;
		this.budget = budget;
		this.round = "straw";
		this.doneWithRound = false;
		this.infoPurchased = new int[numCandidates][2];
		this.votes = new int[numCandidates][3];
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

	public void doneWithRound() {
		this.doneWithRound = true;
	}
	
	public void setRound(String newRound) {
		this.round = newRound;
	}
	
	public String getRound() {
		return this.round;
	}

	public boolean checkIfDone() {
		return this.doneWithRound;
	}

	public void newRound() {
		this.doneWithRound = false;
	}
}
