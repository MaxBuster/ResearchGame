package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket clientSocket;
	private DataInputStream socketInputStream;
	private DataOutputStream socketOutputStream;

	public Client() {
		try {
			// On PC go to Command Prompt; look for IPv4 after ipconfig
			// On Mac go to Terminal; look for inet after ifconfig |grep inet
			// Ignore 127.0.0.1
			clientSocket = new Socket("localhost", 10501);
			socketInputStream = new DataInputStream(clientSocket.getInputStream());
			socketOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			ClientHandler handler = new ClientHandler(socketInputStream, socketOutputStream);
			handler.handleIO();
		} catch (UnknownHostException e) {
			// Throw an error
		} catch (IOException e) {
			// Throw an error
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
	}

}
