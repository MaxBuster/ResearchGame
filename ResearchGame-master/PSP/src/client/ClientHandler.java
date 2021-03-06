package client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jfree.data.xy.IntervalXYDataset;

public class ClientHandler {
	private final PropertyChangeSupport PCS = new PropertyChangeSupport(this);
	private DataInputStream socketInputStream;
	private DataOutputStream socketOutputStream;
	private ClientJFrame gui;

	private final String[] TABLE2BUYNAMES = {"Candidate #", "Price", "Buy"};
	private final String[] TABLE2VOTENAMES = {"Candidate #", "Vote"};
	private final String[] TABLE1NAMES = {"Candidate #", "Party", "Ideal Point", "Straw Votes", "First Round Votes"};

	private Object[][] TABLE2BUYDATA;
	private Object[][] TABLE2VOTEDATA;
	private Object[][] TABLE1DATA;

	private int playerNum;
	private int idealPt;
	private char party;
	private int budget;

	public ClientHandler(DataInputStream socketInputStream, DataOutputStream socketOutputStream) {
		PCS.addPropertyChangeListener(new ChangeListener());
		this.socketInputStream = socketInputStream;
		this.socketOutputStream = socketOutputStream;
		gui = new ClientJFrame(PCS);
		gui.setVisible(true);
	}

	public void handleIO() {
		while (true) {
			try {
				char c = (char) socketInputStream.readByte();
				while (c != '!') {
					c = (char) socketInputStream.readByte();
				}
				int messageType = socketInputStream.readByte();
				if (messageType == 0) { // Getting labels
					getAndSetLabels();
				} else if (messageType == 1) { // Getting graph data
					getAndSetChart();
				} else if (messageType == 2) { // Getting candidates and starting the game
					String gameDescription = "There will five rounds: buy info, straw vote, first vote, buy info, second vote \n"
							+ "The goal is to try to try to get the candidate to win with the closest ideal point to you \n"
							+ "It is currently the first buy round. Candidates with the same party as you cost twice as much";
					gui.setTextPane(gameDescription);
					getAndSetCandidateInfo();
					gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
					gui.setScrollPane2(TABLE2BUYNAMES, TABLE2BUYDATA, "Buy");
					gui.addEndRoundBtn(1);
					gui.updateGUI();
				} else if (messageType == 6) { // Got info
					getAndSetPurchasedInfo();
				} else if (messageType == 8) { // Starting the straw vote
					String strawVoteDescription = "It is now the straw vote. \n"
							+ "Vote based on the information that you bought from the previous round. \n"
							+ "This round will have no effect on the final winner, and is meant to give information about the other voters.";
					gui.setTextPane(strawVoteDescription);
					gui.removeScrollPane2();
					gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
					gui.updateGUI();
				} else if (messageType == 10) {
					startRoundAfterVote();
				} else if (messageType == 11) {
					String finalVoteDescription = "This is the final vote round. \n"
							+ "Whoever wins this round will win the election.";
					gui.setTextPane(finalVoteDescription);
					gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
					gui.updateGUI();
				} else if (messageType == 13) {
					int winningCandidate = socketInputStream.readByte();
					boolean isClosest = socketInputStream.readBoolean();
					if (isClosest) {
						gui.increaseWinnings();
					}
					gui.setTextPane("The winner is: " + winningCandidate);
					sleep();
				} else {
					// Read the rest and ignore
				}
			} catch (IOException e) {
				break; // End client
			}
		}
	}

	private void getAndSetLabels() {
		try {
			playerNum = socketInputStream.readByte();
			party = socketInputStream.readChar();
			idealPt = socketInputStream.readInt();
			budget = socketInputStream.readInt();
			gui.addLabels(playerNum, party, idealPt, budget);
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
		}
	}

	private void getAndSetChart() {
		try {
			int[] chartInfo = new int[4];
			for (int i = 0; i < 4; i++) {
				chartInfo[i] = socketInputStream.readInt();
			}
			double[] chartData = BiModalDist.getData(chartInfo);
			gui.addChart(chartData, idealPt);
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
		}
	}

	private void getAndSetCandidateInfo() {
		try {
			int numCandidates = socketInputStream.readByte();
			TABLE2BUYDATA = new Object[numCandidates][3];
			TABLE2VOTEDATA = new Object[numCandidates][2];
			TABLE1DATA = new Object[numCandidates][5];

			for (int i = 0; i < numCandidates; i++) {
				int candidateNumber = socketInputStream.readByte();
				char candidateParty = (char) socketInputStream.readByte();
				int infoPrice;
				if (candidateParty == party) { 
					infoPrice = 2;
				}
				else {
					infoPrice = 1;
				}
				double[] candidateExpectations  = BetaDist.getBetaDist(0, 0);
				for (int candidate = 0; candidate < numCandidates; candidate++) {
					IntervalXYDataset dataset = MakeChart.createDataset(candidateExpectations, "Candidate " + (candidate+1));
					gui.addDataset(candidate, dataset);
				}
				TABLE2BUYDATA[i] = new Object[] {candidateNumber, infoPrice, "Buy"}; 
				TABLE2VOTEDATA[i] = new Object[] {candidateNumber, "Vote"};
				TABLE1DATA[i] = new Object[] {candidateNumber, candidateParty, "-------", "-------", "-------"};
			}
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
		}
	}

	private void getAndSetPurchasedInfo() {
		try {
			int candidate = socketInputStream.readByte();
			int tokens = socketInputStream.readInt();
			int signals = socketInputStream.readInt();

			int expected = ((signals+1)*100)/(tokens+2);
			double[] beta = BetaDist.getBetaDist(tokens, signals);
			IntervalXYDataset data = MakeChart.createDataset(beta, "Candidate " + (candidate+1));

			addToTable1Data(candidate, 2, expected);
			gui.removeScrollPane1();
			gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
			gui.addDataset(candidate, data);
			gui.updateGUI();
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
		}
	}
	
	private void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// FIXME Do some sort of catch
			e.printStackTrace();
		}
	}

	private void addToTable1Data(int candidateNumber, int position, Object data) {
		for (int i=0; i<TABLE1DATA.length; i++) {
			if (TABLE1DATA[i][0].equals(candidateNumber+1)) {
				TABLE1DATA[i][position] = data;
			}
		}
	}

	private void startRoundAfterVote() {
		try {
			int numCandidates = socketInputStream.readByte();
			int round = socketInputStream.readByte();
			// If its 0 then do straw if its 1 then start buy after
			int[] candNums = new int[numCandidates];
			for (int i = 0; i < numCandidates; i++) {
				int candNum = socketInputStream.readByte();
				int numVotes = socketInputStream.readInt();
				addToTable1Data(candNum-1, round+3, numVotes+"%"); 

				candNums[i] = candNum;
			}
			if (round == 0) {
				String firstVoteDescription = "This is the first real vote. \n"
						+ "The top two candidates from this round will continue to the final vote.";
				gui.setTextPane(firstVoteDescription);
				gui.removeScrollPane1();
				gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
				gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
				gui.updateGUI();
			} else if (round == 1) {
				Object[][] TempTable2BuyData = new Object[2][]; 
				Object[][] TempTable2VoteData = new Object[2][];
				TempTable2BuyData[0] = TABLE2BUYDATA[candNums[numCandidates-1]-1];
				TempTable2VoteData[0] = TABLE2VOTEDATA[candNums[numCandidates-1]-1];
				TempTable2BuyData[1] = TABLE2BUYDATA[candNums[numCandidates-2]-1];
				TempTable2VoteData[1] = TABLE2VOTEDATA[candNums[numCandidates-2]-1];
				TABLE2BUYDATA = TempTable2BuyData;
				TABLE2VOTEDATA = TempTable2VoteData;

				String secondBuyDescription = "This is the final information purchase round.";
				gui.setTextPane(secondBuyDescription);
				gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
				gui.setScrollPane2(TABLE2BUYNAMES, TABLE2BUYDATA, "Buy");
				gui.addEndRoundBtn(0);
				gui.updateGUI();
			}
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
		}
	}

	class ChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent PCE) {
			if (PCE.getPropertyName() == "Buy Info") {
				int candidateToBuyFrom = (Integer) PCE.getOldValue();
				buyInfo(candidateToBuyFrom);
			} else if (PCE.getPropertyName() == "Vote") {
				int candidateToVoteFor = (Integer) PCE.getOldValue();
				voteForCandidate(candidateToVoteFor);
			} else if (PCE.getPropertyName() == "End Round") {
				try {
					int whichRound = (Integer) PCE.getOldValue();
					socketOutputStream.writeChar((int) '!');
					socketOutputStream.writeByte(7); // End buy round
					socketOutputStream.writeByte(whichRound); // Which buy round
					// is ending
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void buyInfo(int candNum) {
		try {
			socketOutputStream.writeChar((int) '!');
			socketOutputStream.writeByte(5);
			socketOutputStream.writeInt(candNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void voteForCandidate(int candNum) {
		try {
			socketOutputStream.writeChar((int) '!');
			socketOutputStream.writeByte(9);
			socketOutputStream.writeByte(candNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
