package server;

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

	public Server() {
		try {
			serverSocket = new ServerSocket(10501);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
