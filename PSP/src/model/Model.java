package model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Model {
	private PropertyChangeSupport PCS;
	private static int playerNumber;
	private static int candidateNumber;
	private int[] sumPoints;
	private int sumDataPoints;
	private ArrayList<Player> players;
	private boolean dataWritten = false;
	private boolean startGame = true;
	private int roundNum = 0;
	private String[] roundNames = new String[]{"First Buy", "Straw Vote", "First Vote", "Second Buy", "Second Vote", "Over"};
	private int gameNum = 0;
	private ArrayList<GameInfo> gameInfo;
	
	public Model(final PropertyChangeSupport PCS, ArrayList<GameInfo> gameInfo) {
		this.PCS = PCS;
		this.gameInfo = gameInfo;
		players = new ArrayList<Player>();
		setNewGame(gameInfo.get(0));
	}
	
	public synchronized void addPlayerToGameObject(Player player, int currentGame) {
		gameInfo.get(currentGame).addPlayer(player);
	}
	
	public synchronized void getNewGame(int gameNum) {
		if (this.gameNum < gameNum) {
			PCS.firePropertyChange("New Round", null, roundNames[0]);
			PCS.firePropertyChange("New Game", null, gameNum);
			this.gameNum++;
			setNewGame(gameInfo.get(gameNum));
		}
	}
	
	public void setNewGame(GameInfo game) { 
		setGraphData(game.getDistribution());
		int[] candIdealPts = game.getIdealPts();
		char[] candParties = game.getParties();
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		for (int i = 0; i < candIdealPts.length; i++) {
			Candidate candidate = new Candidate(i+1, candParties[i], candIdealPts[i]);
			candidates.add(candidate);
		}
		gameInfo.get(gameNum).setCandidates(candidates);
	}
	
	public void setGraphData(int[] graphData) {
		GetDistribution distribution = new GetDistribution(graphData);
		sumPoints = distribution.getSumData();
		sumDataPoints = distribution.getSum();
	}
	
	public int getBudget() {
		return gameInfo.get(gameNum).getBudget();
	}

	public synchronized boolean getStartGame() {
		return startGame;
	}
	
	public synchronized void setStartGame(boolean newStartGame) {
		startGame = newStartGame;
	}
	
	public int[] getData() {
		return gameInfo.get(gameNum).getDistribution();
	}

	public int getCandidateNumber() {
		candidateNumber++;
		return candidateNumber;
	}

	public ArrayList<Candidate> getCandidates() {
		return gameInfo.get(gameNum).getCandidates();
	}

	public synchronized Player newPlayer() {
		int playerNum = getPlayerNumber();
		char party = getParty();
		int idealPt = getIdealPt();
		Player player = new Player(playerNum, party, idealPt, getBudget(), gameInfo.get(gameNum).getNumCandidates());
		players.add(player);
		PCS.firePropertyChange("New Player", null, playerNum);
		return player;
	}
	
	public void resetPlayer(Player player) {
		char party = getParty();
		int idealPt = getIdealPt();
		player.resetPlayer(party, idealPt, getBudget(), gameInfo.get(gameNum).getNumCandidates());
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
		PCS.firePropertyChange("Removed Player", null, player.getPlayerNumber());
	}

	public int getPlayerNumber() {
		playerNumber++;
		return playerNumber;
	}

	public int getIdealPt() {
		double randomNum = Math.random();
		double unroundedPt = randomNum * sumDataPoints;
		for (int i=0; i<100; i++) {
			if (sumPoints[i] > unroundedPt) {
				return i;
			}
		}
		return 100;
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
		return gameInfo.get(gameNum).getCandidates().get(candNum);
	}
	
	public int getNumPlayers() {
		return players.size();
	}
	
	public Player getPlayer(int playerNum) {
		for (Player player : players) {
			if (player.getPlayerNumber() == playerNum) {
				return player;
			}
		}
		return null;
	}
	
	public ArrayList<Candidate> getSortedCandidates() {
		ArrayList<Candidate> tempCands = new ArrayList<Candidate>(gameInfo.get(gameNum).getCandidates());
		for (int i=0; i<tempCands.size(); i++) {
			Candidate currentI = tempCands.get(i);
			for (int j=i; j<tempCands.size(); j++) {
				Candidate currentJ = tempCands.get(j);
				if (currentI.getFirstVotes() > currentJ.getFirstVotes()) {
					tempCands.set(j, currentI);
					tempCands.set(i, currentJ);
				}
			}
		}
		return tempCands;
	}
	
	public Candidate getWinner(ArrayList<Candidate> candidates) {
		for (int i=0; i<candidates.size(); i++) {
			Candidate currentI = candidates.get(i);
			for (int j=i; j<candidates.size(); j++) {
				Candidate currentJ = candidates.get(j);
				if (currentI.getSecondVotes() > currentJ.getSecondVotes()) {
					candidates.set(j, currentI);
					candidates.set(i, currentJ);
				}
			}
		}
		return candidates.get(candidates.size()-1);
	}
	
	public boolean winnerIsClosest(int playerIdeal, Candidate candidate) {
		boolean isClosest = true;
		int diffToBeat = Math.abs(playerIdeal-candidate.getIdealPt());
		for (Candidate other : gameInfo.get(gameNum).getCandidates()) {
			int currentDiff = Math.abs(playerIdeal-other.getIdealPt());
			if (currentDiff < diffToBeat) {
				isClosest = false;
			}
		}
		return isClosest;
	}

	public synchronized boolean checkEndRound() {
		boolean done = true;
		for (Player player : players) {
			if (!player.checkIfDone()) {
				done = false;
			}
		}
		if (done == true) {
			roundNum = (roundNum+1)%6;
			PCS.firePropertyChange("New Round", null, this.roundNames[roundNum]);
		}
		return done;
	}
	
	public int getNumGames() {
		return gameInfo.size();
	}
	
	public synchronized void writeDataOut() {
		if (!dataWritten) {
			dataWritten = true;
			WriteDataOut.writeData(gameInfo);
		}
	}
}
