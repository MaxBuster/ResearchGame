package model;

public class Player {
	private int playerNum;
	private char playerParty;
	private int idealPt;
	private int budget;
	
	private int[][] info;
	private int[] votes;
	
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
		this.info = new int[numCandidates][2];
		this.votes = new int[3];
	}
	
	public void resetPlayer(char playerParty, int idealPt, int budget, int numCandidates) {
		this.playerParty = playerParty;
		this.idealPt = idealPt;
		this.budget = budget;
		this.round = "straw";
		this.doneWithRound = false;
		this.info = new int[numCandidates][2];
		this.votes = new int[3];
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
	
	public void addInfo(int candidateNum, int info) {
		this.info[candidateNum][info]++;
	}
	
	public int[] getInfo(int candidateNum) {
		return this.info[candidateNum];
	}
	
	public int[][] getInfo() {
		return this.info;
	}

	public void doneWithRound() {
		this.doneWithRound = true;
	}
	
	public void addVote(int candidateNum, int roundNum) {
		this.votes[roundNum] = candidateNum+1;
	}
	
	public int[] getVotes() {
		return this.votes;
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
