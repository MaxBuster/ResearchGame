package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
		// FIXME Wait to do this until the game has started
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
						model.writeDataOut();
					}
				} else {
					// Exceptions?
				}
			} catch (IOException e) {
				break; // End client
			}
		}
	}
	
	private void startGame() {
		try {
			writeMessage(0, player.getPlayerNumber());
			out.writeChar(player.getParty());
			out.writeInt(player.getIdealPt());
			out.writeInt(player.getBudget());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		writeChartData();
		Candidate[] candidates = model.getCandidates();
		writeMessage(2, candidates.length);
		for (int i = 0; i < candidates.length; i++) {
			try {
				out.writeByte(candidates[i].getCandidateNumber());
				out.writeByte(candidates[i].getParty());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void returnInfo() {
		try {
			int candidateToBuyFrom = in.readInt();
			Candidate candidate = model.getCandidate(candidateToBuyFrom);
			int lowerBound = candidate.getLowerBound(); // FIXME generate these from the normal dist.
			int upperBound = candidate.getUpperBound();

			writeMessage(6, candidateToBuyFrom);
			out.writeInt(lowerBound);
			out.writeInt(upperBound);
		} catch (IOException e) {
			// Create alert about it
		}
	}

	private void voteForCandidate() {
		try {
			int candidateToVoteFor = in.readByte();
			if (player.getRound() == "first") {
				model.getCandidate(candidateToVoteFor).voteFirst();
			} else if (player.getRound() == "final") {
				model.getCandidate(candidateToVoteFor).voteSecond();
			} else {
				model.getCandidate(candidateToVoteFor).voteStraw();
			}
		}  catch (IOException e) {
			// Create alert about it
		}
	}

	private void startFirstVote() {
		try {
			Candidate[] candidates = model.getCandidates();
			player.setRound("first");
			writeMessage(10, candidates.length);
			out.writeByte(0);
			for (Candidate candidate : candidates) {
				out.writeByte(candidate.getCandidateNumber());
				out.writeByte(candidate.getStrawVotes());
			}
		}  catch (IOException e) {
			// Create alert about it
		}
	}

	private void startSecondBuy() {
		try {
			Candidate[] candidates = model.getCandidates();
			for (int i = 0; i < candidates.length; i++) { // FIXME Do this with a java sorter
				Candidate thisCand = candidates[i];
				for (int j = 0; j < candidates.length; j++) {
					Candidate nextCand = candidates[j];
					if (thisCand.getFirstVotes() > nextCand
							.getFirstVotes()) {
						candidates[j] = thisCand;
						candidates[i] = nextCand;
					}
				}
			}
			writeMessage(10, 2); // 2 means that you write out the top two cands
			out.writeByte(1);
			for (int i = 3; i > 1; i--) { // This writes the top two cands
				out.writeByte(candidates[i].getCandidateNumber());
				out.writeByte(candidates[i].getFirstVotes());
			}
		}  catch (IOException e) {
			// Create alert about it
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
			e.printStackTrace();
		}
	}
	
	private void sendWinner() {
		Candidate[] candidates = model.getCandidates();
		if (candidates[3].getSecondVotes() > candidates[2].getSecondVotes()) { // FIXME Future proof this for >2 cands
			writeMessage(13, candidates[3].getCandidateNumber()); // Writes out the winner
		} else {
			writeMessage(13, candidates[2].getCandidateNumber()); // Writes out the winner
		}
	}

	private void writeMessage(int type, int message) {
		try {
			out.writeByte((int) '!');
			out.writeByte(type);
			out.writeByte(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
