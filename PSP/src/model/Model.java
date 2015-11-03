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
	private ArrayList<Candidate> candidates;
	private int[] sumPoints;
	private int sumDataPoints;
	private ArrayList<Player> players;
	private boolean dataWritten = false;
	private boolean startGame = true;
	private String fileName = "researchData.json";
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
			this.gameNum++;
			setNewGame(gameInfo.get(gameNum));
		}
	}
	
	public void setNewGame(GameInfo game) {
		setGraphData(game.getDistribution());
		int[] currentCands = game.getIdealPts();
		candidates = new ArrayList<Candidate>();
		for (int i = 0; i < currentCands.length; i++) {
			Candidate candidate = new Candidate(i+1, getParty(), currentCands[i]);
			candidates.add(candidate);
		}
	}
	
	public void setGraphData(int[] graphData) {
		GetDistribution distribution = new GetDistribution(graphData);
		sumPoints = distribution.getSumData();
		sumDataPoints = distribution.getSum();
	}
	
	public int getBudget() {
		return gameInfo.get(gameNum).getBudget();
	}

	public boolean getStartGame() {
		return startGame;
	}
	
	public void setStartGame(boolean newStartGame) {
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
		return candidates;
	}

	public synchronized Player newPlayer() {
		int playerNum = getPlayerNumber();
		char party = getParty();
		int idealPt = getIdealPt();
		Player player = new Player(playerNum, party, idealPt, getBudget(), candidates.size());
		players.add(player);
		PCS.firePropertyChange("New Player", null, playerNum);
		return player;
	}
	
	public void resetPlayer(Player player) {
		char party = getParty();
		int idealPt = getIdealPt();
		player.resetPlayer(party, idealPt, getBudget(), candidates.size());
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
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
		return candidates.get(candNum);
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
		ArrayList<Candidate> tempCands = new ArrayList<Candidate>(this.candidates);
		Collections.sort(tempCands, new Comparator<Candidate> () {
			@Override
			public int compare(Candidate candidate1, Candidate candidate2) {
				if (candidate1.getFirstVotes() > candidate2.getFirstVotes()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return tempCands;
	}
	
	public Candidate getWinner() {
		ArrayList<Candidate> tempCands = new ArrayList<Candidate>(this.candidates);
		Collections.sort(tempCands, new Comparator<Candidate> () {
			@Override
			public int compare(Candidate candidate1, Candidate candidate2) {
				if (candidate1.getSecondVotes() > candidate2.getSecondVotes()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return tempCands.get(candidates.size()-1);
	}
	
	public boolean winnerIsClosest(int playerIdeal, Candidate candidate) {
		boolean isClosest = true;
		int diffToBeat = Math.abs(playerIdeal-candidate.getIdealPt());
		for (Candidate other : candidates) {
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
