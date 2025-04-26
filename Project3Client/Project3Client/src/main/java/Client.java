import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;




public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;
	TextArea chatLog;
	TextField chatInput;
	boolean	selfMessage = false;
	GuiClient gui;

	public Client(TextArea chatLog, GuiClient gui) {
		this.chatLog = chatLog;
		this.gui = gui;
	}


	public void run() {

		try {
			socketClient= new Socket("127.0.0.1",5555);
	    	out = new ObjectOutputStream(socketClient.getOutputStream());
	    	in = new ObjectInputStream(socketClient.getInputStream());
	   	 	socketClient.setTcpNoDelay(true);

		}
		catch(Exception e) {}

		while(true) {

			try {
				String message = in.readObject().toString();
				System.out.println(message);
				if (message.equals("USERNAME_TAKEN")) {
					chatLog.appendText("Username is already taken! Please restart and choose another username.\n");
					socketClient.close();
					break; // ⬅️ stop the Client thread
				}
				if (message.equals("WAIT_FOR_PLAYERS")) {
					gui.switchToGameScene(); // NEW 🔥 still switch if you are first
					chatLog.appendText("Waiting for another player...\n");
				} else if (message.startsWith("MOVE ")) {
					int col = Integer.parseInt(message.substring(5)); // Extract number
					gui.opponentMove(col); // Tell GUI opponent made a move
					continue;
				} else if (message.equals("YOUR_TURN")) {
					gui.setYourTurn(true);
					gui.switchToGameScene(); // NEW 🔥 switch when ready
					continue;
				} else if (message.equals("WAIT")) {
					gui.setYourTurn(false);
					gui.switchToGameScene(); // 🔥 ADD THIS LINE
					continue;
				} else if (message.startsWith("ROOM_LIST ")) {
					String rooms = message.substring(10);
					javafx.application.Platform.runLater(() -> {
						gui.showRoomButtons(rooms);
					});

				}
				else if (message.equals("OPPONENT_DISCONNECTED")) {
					chatLog.appendText("Your opponent has disconnected.\n");
					gui.returnButton.setVisible(true);
					gui.disableButtons();
					gui.turn.setText("Opponent left. You win by default!");
				}


				else if (message.equals("JOIN_FAILED")) {
					chatLog.appendText("Room not found! Please refresh.\n");
				}else {
					if (!selfMessage) {
						chatLog.appendText("Other: " + message + "\n");
					}
					selfMessage = false;
				}
			}
			catch(Exception e) {}
		}

    }

	public void send(String data) {

		try {
			selfMessage = true; //since we first send this is triggered
			out.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to send message to server!");
			e.printStackTrace();
		}
	}
}
