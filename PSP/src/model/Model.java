package model;

import java.util.ArrayList;

public class Model {
	private static int playerNumber;
	private static int candidateNumber;
	private static final int BUDGET = 2000;
	private Candidate[] candidates;
	private int[] points;
	private ArrayList<Player> players;
	private String round;

	public Model() {
		candidates = new Candidate[4];
		for (int i = 0; i < 4; i++) {
			candidates[i] = newCandidate();
		}
		players = new ArrayList<Player>();
		BiModalDist distribution = new BiModalDist(40, 40, 60);
		points = distribution.getData();
		round = "Buy";
		// int totalPoints = distribution.getTotalPoints(); Use this?
	}

	public int[] getData() {
		return points;
	}

	public Candidate newCandidate() {
		int candNum = getCandidateNumber();
		char party = getParty();
		int idealPt = getIdealPt();
		Candidate candidate = new Candidate(candNum, party, idealPt);
		return candidate;
	}

	public int getCandidateNumber() {
		candidateNumber++;
		return candidateNumber;
	}

	public Candidate[] getCandidates() {
		return candidates;
	}

	public Player newPlayer() {
		int playerNum = getPlayerNumber();
		char party = getParty();
		int idealPt = getIdealPt();
		Player player = new Player(playerNum, party, idealPt, BUDGET);
		players.add(player);
		return player;
	}

	public int getPlayerNumber() {
		playerNumber++;
		return playerNumber;
	}

	public int getIdealPt() {
		double randomNum = Math.random();
		double unroundedPt = randomNum * 100;
		int idealPt = (int) unroundedPt;
		return idealPt;
	}

	public char getParty() {
		double randomNum = Math.random();
		if (randomNum < .5) {
			return 'R';
		} else {
			return 'D';
		}
	}

	public Candidate getCandidate(int candNum) {
		return candidates[candNum];
	}

	public synchronized boolean checkEndRound() {
		boolean done = true;
		for (Player player : players) {
			if (!player.checkIfDone()) {
				done = false;
			}
		}
		return done;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String newRound) {
		round = newRound;
	}
}
