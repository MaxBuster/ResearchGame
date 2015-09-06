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

import org.jfree.data.xy.IntervalXYDataset;

import model.BiModalDist;
import model.Model;

public class Server {
	private static final Model MODEL = new Model();
	private static ServerSocket serverSocket;
	private final PropertyChangeSupport PCS = new PropertyChangeSupport(this);
	private static ServerJFrame gui;

	public Server() {
		try {
			serverSocket = new ServerSocket(10501);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PCS.addPropertyChangeListener(new ChangeListener());
		
		MODEL.setStartGame(false);
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
					ServerHandler serverHandler = new ServerHandler(MODEL, in,
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
				MODEL.setGraphData(graphData);
			} else if (PCE.getPropertyName() == "Set Num Candidates") {
				int numCandidates = (Integer) PCE.getNewValue();
				MODEL.setNumCandidates(numCandidates);
			} else if (PCE.getPropertyName() == "Set Budget") {
				int budget = (Integer) PCE.getNewValue();
				MODEL.setBudget(budget);
			} else if (PCE.getPropertyName() == "Set File Name") {
				String fileName = (String) PCE.getNewValue();
				MODEL.setFileName(fileName);
			} else if (PCE.getPropertyName() == "Start Game") {
				MODEL.setStartGame(true);
				ServerHandler.notifyWaiters();
				gui.setRound("First Buy");
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
