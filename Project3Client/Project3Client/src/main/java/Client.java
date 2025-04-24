import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.scene.control.TextArea;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;
	TextArea chatLog;
	boolean	selfMessage = false;

	public Client(TextArea chatLog) {
		this.chatLog = chatLog;
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
				if (!selfMessage){
					chatLog.appendText("Other: " + message + "\n");
				}
				selfMessage = false;
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
			e.printStackTrace();
		}
	}
}
