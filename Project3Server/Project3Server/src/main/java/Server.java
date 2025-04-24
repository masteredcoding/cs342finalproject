import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.List;



public class Server{


//	private List<String> clientsUsernames = new ArrayList<>();
//
//	public boolean isUsernameTaken(String username) {
//		return clientsUsernames.contains(username);  // Check if username exists in the shared list
//	}

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;


	
	Server(){

		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");


		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			} catch(Exception e) {
					System.err.println("Server did not launch");
				}
			}
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			String username;
			ObjectInputStream in;
			List <String> arrayList = new ArrayList<>();

			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(String message) {
				//TODO implement
				for (ClientThread c : clients) {
					try {
						c.out.writeObject(message);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			public void run(){
					
				try {

					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
					username = in.readObject().toString();
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
//				if (isUsernameTaken(username)) {
//					System.out.println("taken");
//				}else{
//					clientsUsernames.add(username);
//					System.out.println("not taken");
//				}

				System.out.println("Client #" + count + " set username to: " + username);
				updateClients("new client on server: client #" + count);
					
				 while(true) {
					    try {
					    	String data = in.readObject().toString();
					    	System.out.println("client: " + username + " sent: " + data);
					    	updateClients("client #"+username+" said: "+data);
					    	
					    	}
					    catch(Exception e) {
					    	System.err.println("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
