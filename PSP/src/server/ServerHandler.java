package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import model.Candidate;
import model.Model;
import model.Player;

public class ServerHandler {
	private Model model;
	private Player player;
	private DataInputStream in;
	private DataOutputStream out;
	private static Object waitObject = new Object();

	public ServerHandler(Model model, DataInputStream in, DataOutputStream out) {
		this.model = model;
		this.player = model.newPlayer();
		this.in = in;
		this.out = out;
	}

	public void handleIO() {
		waitForGameStart();
		startGame();
		while (true) {
			try {
				char c = (char) in.readByte();
				while (c != '!') {
					c = (char) in.readByte();
				}
				int messageType = in.readByte();
				if (messageType == 5) { // Bought info
					returnInfo();
				} else if (messageType == 7) { // Ended a buy round
					int whichBuyRound = in.readByte();
					waitForNewRound(); // Waits for all players to be done with the round
					setRound(whichBuyRound); // Sets the round in the player's data
					startRound(whichBuyRound); // Sends out a starting round message
				} else if (messageType == 9) {
					voteForCandidate();
					waitForNewRound();
					if (player.getRound() == "straw") {
						startFirstVote();
					} else if (player.getRound() == "first") {
						startSecondBuy();
					} else {
						sendWinner();
						model.writeDataOut(); // FIXME is this synchronized?
					}
				} else {
					// Exceptions?
				}
			} catch (IOException e) {
				removePlayer();
				break; // End client
			}
		}
	}
	
	private void startGame() {
		try {
			writeMessage(0, player.getPlayerNumber());
			out.writeChar(player.getParty());
			out.writeInt(player.getIdealPt());
			out.writeInt(model.getBudget());
		} catch (IOException e1) {
			removePlayer();
		}
		writeChartData();
		ArrayList<Candidate> candidates = model.getCandidates();
		writeMessage(2, candidates.size());
		for (int i = 0; i < candidates.size(); i++) {
			try {
				out.writeByte(candidates.get(i).getCandidateNumber());
				out.writeByte(candidates.get(i).getParty());
			} catch (IOException e) {
				removePlayer();
			}
		}
	}

	private void returnInfo() {
		try {
			int candidateToBuyFrom = in.readInt();
			Candidate candidate = model.getCandidate(candidateToBuyFrom);
			int lowerBound = candidate.getLowerBound(); // FIXME generate these from the normal dist.
			int upperBound = candidate.getUpperBound();
			player.addInfo(candidateToBuyFrom, 1);

			writeMessage(6, candidateToBuyFrom);
			out.writeInt(lowerBound);
			out.writeInt(upperBound);
		} catch (IOException e) {
			removePlayer();
		}
	}

	private void voteForCandidate() {
		try {
			int candidateToVoteFor = in.readByte();
			if (player.getRound() == "first") {
				model.getCandidate(candidateToVoteFor).voteFirst();
				player.addVote(candidateToVoteFor, 1);
			} else if (player.getRound() == "final") {
				model.getCandidate(candidateToVoteFor).voteSecond();
				player.addVote(candidateToVoteFor, 2);
			} else {
				model.getCandidate(candidateToVoteFor).voteStraw();
				player.addVote(candidateToVoteFor, 0);
			}
		}  catch (IOException e) {
			removePlayer();
		}
	}

	private void startFirstVote() {
		try {
			ArrayList<Candidate> candidates = model.getCandidates();
			player.setRound("first");
			writeMessage(10, candidates.size());
			out.writeByte(0);
			int numPlayers = model.getNumPlayers();
			for (Candidate candidate : candidates) {
				out.writeByte(candidate.getCandidateNumber());
				int numVotes = candidate.getStrawVotes();
				int percentVotes = ((numVotes*100)/numPlayers);
				out.writeInt(percentVotes);
			}
		}  catch (IOException e) {
			removePlayer();
		}
	}

	private void startSecondBuy() {
		try {
			ArrayList<Candidate> candidates = model.getSortedCandidates();
			writeMessage(10, candidates.size()); 
			out.writeByte(1);
			int numPlayers = model.getNumPlayers();
			for (Candidate candidate : candidates) { // This writes the top candidates
				out.writeByte(candidate.getCandidateNumber());
				int numVotes = candidate.getFirstVotes();
				int percentVotes = ((numVotes*100)/numPlayers);
				out.writeInt(percentVotes);
			}
		}  catch (IOException e) {
			removePlayer();
		}
	}
	
	private void waitForGameStart() {
		synchronized (waitObject) {
			if (!model.getStartGame()) {
				try {
					waitObject.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			waitObject.notifyAll();
		}
	}
	
	public static void notifyWaiters() {
		synchronized (waitObject) {
			waitObject.notifyAll();
		}
	}

	private void waitForNewRound() {
		player.doneWithRound();
		synchronized (waitObject) {
			if (!model.checkEndRound()) {
				try {
					waitObject.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			waitObject.notifyAll();
		}
		player.newRound();
	}

	private void setRound(int whichBuyRound) {
		if (whichBuyRound == 1) {
			player.setRound("straw");
		} else {
			player.setRound("final");
		}
	}

	private void startRound(int whichBuyRound) {
		try {
			out.writeByte((int) '!');
			if (whichBuyRound == 1) {
				out.writeByte(8); // This starts straw round
			} else {
				out.writeByte(11); // This starts final round
			}
		} catch (IOException e) {
			removePlayer();
		}
	}

	private void writeChartData() {
		int[] chartData = model.getData();
		int chartSize = chartData.length;
		try {
			out.writeByte((int) '!');
			out.writeByte(1);
			out.writeInt(chartSize);
			for (int i = 0; i < chartSize; i++) {
				out.writeInt(chartData[i]);
			}
		} catch (IOException e) {
			removePlayer();
		}
	}
	
	private void sendWinner() {
		Candidate winner = model.getWinner();
		writeMessage(13, winner.getCandidateNumber()); // Writes out the winner
		boolean isClosest = model.winnerIsClosest(player.getIdealPt(), winner);
		try {
			out.writeBoolean(isClosest);
		} catch (IOException e) {
			removePlayer();
		}
	}

	private void writeMessage(int type, int message) {
		try {
			out.writeByte((int) '!');
			out.writeByte(type);
			out.writeByte(message);
		} catch (IOException e) {
			removePlayer();
		}
	}
	
	private void removePlayer() {
		model.removePlayer(player);
		if (model.checkEndRound()) {
			notifyWaiters();
		}
	}
}
