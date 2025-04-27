import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Client extends Thread {

	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	TextArea chatLog;
	TextField chatInput;
	boolean selfMessage = false;
	private GuiClient guiClient;

	// Add game status
	private boolean myTurn = false;

	public Client(TextArea chatLog) {
		this.chatLog = chatLog;
	}
	public void setGuiClient(GuiClient guiClient) {
		this.guiClient = guiClient;
	}


	public void run() {
		try {
			socketClient = new Socket("127.0.0.1", 5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				String message = (String) in.readObject();
				System.out.println("Server sent: " + message);

				processMessage(message);

			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	private void processMessage(String message) {
		if (message.equals("WAIT_FOR_PLAYERS")) {
			chatLog.appendText("Waiting for another player...\n");
		} else if (message.equals("YOUR_TURN")) {
			myTurn = true;
			chatLog.appendText("Your turn!\n");
			// Here you can call a method to enable drop buttons
		} else if (message.equals("WAIT")) {
			myTurn = false;
			chatLog.appendText("Waiting for opponent's move...\n");
			// Here you can call a method to disable drop buttons
		} else if (message.startsWith("MOVE")) {
			String moveData = message.substring(5);
			int col = Integer.parseInt(moveData);

			if (guiClient != null) {
				boolean isMyMove = myTurn; // because we switch turns after sending
				guiClient.applyMove(col, isMyMove);
			}
		}
		else if (message.startsWith("CHAT ")) {
			String chatData = message.substring(5);
			chatLog.appendText("Other: " + chatData + "\n");
		}
		else {
			if (!selfMessage) {
				chatLog.appendText("Other: " + message + "\n");
			}
			selfMessage = false;
		}
	}

	public void send(String data) {
		try {
			selfMessage = true;
			out.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isMyTurn() {
		return myTurn;
	}
}
