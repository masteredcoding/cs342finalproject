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

	int currentTurnIndex = 0;


	
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
			
			int playerNum;
			Socket connection;
			int count;
			String username;
			ObjectInputStream in;
			List <String> arrayList = new ArrayList<>();

			ObjectOutputStream out;

			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;
				this.playerNum = count - 1;
			}
			public void send(String data) {
				try {
					out.writeObject(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
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

				synchronized(clients) { // prevent concurrent access issues
					if (clients.size() == 1) {
						send("WAIT_FOR_PLAYERS");
					} else if (clients.size() == 2) {
						clients.get(currentTurnIndex).send("YOUR_TURN");
						clients.get((currentTurnIndex + 1) % 2).send("WAIT");
					}
				}
					
				 while(true) {
					    try {
					    	String data = in.readObject().toString();

							if (clients.size() < 2) {
								send("WAIT_FOR_PLAYERS");
								continue;
							}

							if (clients.get(currentTurnIndex) == this) {
								System.out.println("client: " + username + " sent: " + data);
								updateClients("client #"+username+" said: "+data);
								currentTurnIndex = (currentTurnIndex + 1) % clients.size(); //calc whose turn it is
								clients.get(currentTurnIndex).send("YOUR_TURN"); // this is curr player
								for (int i = 0; i < clients.size(); i++) {
									if (i != currentTurnIndex) {
										clients.get(i).send("WAIT"); // if its not their turn they wait.
									}
								}
							}else {
								send("WAIT_YOUR_TURN");
							}

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


	
	

	
