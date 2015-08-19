package client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
					getAndSetCandidateInfo();
					gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
					gui.setScrollPane2(TABLE2BUYNAMES, TABLE2BUYDATA, "Buy");
					gui.addEndRoundBtn(1);
					gui.updateGUI();
				} else if (messageType == 6) { // Got info
					getAndSetPurchasedInfo();
				} else if (messageType == 8) { // Starting the straw vote
					gui.removeScrollPane2();
					gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
					gui.updateGUI();
				} else if (messageType == 10) {
					startRoundAfterVote();
				} else if (messageType == 11) {
					gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
					gui.updateGUI();
				} else if (messageType == 13) {
					int winningCandidate = socketInputStream.readByte() + 1; // FIXME This gets the wrong value
					gui.setTextPane("The winner is: " + winningCandidate);
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
			int numPoints = socketInputStream.readInt();
			int[] chartData = new int[numPoints];
			for (int i = 0; i < numPoints; i++) {
				chartData[i] = socketInputStream.readInt();
			}
			gui.addChart(chartData);
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
				if (candidateParty == party) { // FIXME change price based on budget too
					infoPrice = 1000;
				}
				else {
					infoPrice = 500;
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
			int lowerBound = socketInputStream.readInt();
			int upperBound = socketInputStream.readInt();
			String info = lowerBound + " - " + upperBound;
			addToTable1Data(candidate, 2, info);
			gui.removeScrollPane1();
			gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
			gui.updateGUI();
		} catch (IOException e) {
			// Alert that it couldn't retrieve the player data
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
			for (int i = 0; i < numCandidates; i++) {
				int candNum = socketInputStream.readByte();
				int numVotes = socketInputStream.readByte();
				addToTable1Data(candNum-1, round+3, numVotes); 
				// FIXME add to the cell based on the round + standardize cand locations
			}
			if (round == 0) {
				gui.removeScrollPane1();
				gui.setScrollPane1(TABLE1NAMES, TABLE1DATA);
				gui.setScrollPane2(TABLE2VOTENAMES, TABLE2VOTEDATA, "Vote");
				gui.updateGUI();
			} else if (round == 1) {
				gui.removeScrollPane1();
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
