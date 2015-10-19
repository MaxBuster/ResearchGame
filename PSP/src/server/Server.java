package server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.jfree.data.xy.IntervalXYDataset;

import model.BiModalDist;
import model.GameInfo;
import model.Model;
import model.Player;
import model.ReadConfig;

public class Server {
	private final PropertyChangeSupport PCS = new PropertyChangeSupport(this);
	private Model model;
	private static ServerSocket serverSocket;
	private static ServerJFrame gui;

	public Server() {
		ArrayList<GameInfo> gameInfo = ReadConfig.readFile();
		this.model = new Model(PCS, gameInfo);
		
		try {
			serverSocket = new ServerSocket(10501);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PCS.addPropertyChangeListener(new ChangeListener());
		
		// model.setStartGame(false); FIXME change this back in the future
		gui = new ServerJFrame(PCS);
		gui.setVisible(true);
	}

	public void run() {
		try {
			Socket clientSocket = null;
			while (true) {
				clientSocket = serverSocket.accept();
				newThread(clientSocket);
			}
		} catch (IOException e) {
			// Throw error
		}
	}

	public void newThread(final Socket clientSocket) {
		Thread thread = new Thread() {
			public void run() {
				try {
					DataInputStream in = new DataInputStream(
							clientSocket.getInputStream());
					DataOutputStream out = new DataOutputStream(
							clientSocket.getOutputStream());
					ServerHandler serverHandler = new ServerHandler(model, in,
							out);
					serverHandler.handleIO();
				} catch (EOFException e) {
					// Throw error
				} catch (IOException e) {
					// Throw error
				}
			}
		};
		thread.start();
	}
	
	class ChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent PCE) {
			if (PCE.getPropertyName() == "Set Graph Data") {
				int[] graphData = (int[]) PCE.getNewValue();
				model.setGraphData(graphData);
			} else if (PCE.getPropertyName() == "Add Candidate") {
				int idealPt = (Integer) PCE.getNewValue();
				model.addCandidate(idealPt);
			} else if (PCE.getPropertyName() == "Set Budget") {
				int budget = (Integer) PCE.getNewValue();
			} else if (PCE.getPropertyName() == "Set File Name") {
				String fileName = (String) PCE.getNewValue();
				model.setFileName(fileName);
			} else if (PCE.getPropertyName() == "Start Game") {
				model.setStartGame(true);
				model.startGame();
				ServerHandler.notifyWaiters();
				gui.setRound("First Buy");
			} else if (PCE.getPropertyName() == "New Player") {
				int playerNumber = (Integer) PCE.getNewValue();
				gui.addRowToPlayers(playerNumber);
			} else if (PCE.getPropertyName() == "New Round") {
				String newRound = (String) PCE.getNewValue();
				gui.setRound(newRound);
			} else if (PCE.getPropertyName() == "Remove Player") {
				int playerNumber = (Integer) PCE.getNewValue();
				Player playerToRemove = model.getPlayer(playerNumber);
				if (playerToRemove != null) {
					model.removePlayer(playerToRemove);
				}
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
