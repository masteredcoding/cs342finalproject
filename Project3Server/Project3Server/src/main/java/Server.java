import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	int count = 1;
	ArrayList<ClientThread> waitingPlayers = new ArrayList<>();
	TheServer server;
	private List<String> usernames = new ArrayList<>();

	Server() {
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread {
		public void run() {
			try (ServerSocket mysocket = new ServerSocket(5555)) {
				System.out.println("Server is waiting for a client!");

				while (true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					c.start();
					count++;
				}
			} catch (Exception e) {
				System.err.println("Server did not launch");
			}
		}
	}

	class ClientThread extends Thread {
		int playerNum;
		Socket connection;
		int count;
		String username;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count) {
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

		public String receive() throws Exception {
			return (String) in.readObject();
		}

		public void connectionCleanup() {
			try {
				connection.close();
			} catch (Exception e) {
			}
		}

		public void run() {
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);

				username = in.readObject().toString();

				synchronized (usernames) {
					if (usernames.contains(username)) {
						send("USERNAME_TAKEN");
						connection.close();
						return;
					} else {
						usernames.add(username);
					}
				}

				System.out.println("Client #" + count + " set username to: " + username);

				synchronized (waitingPlayers) {
					waitingPlayers.add(this);
					if (waitingPlayers.size() >= 2) {
						ClientThread p1 = waitingPlayers.remove(0);
						ClientThread p2 = waitingPlayers.remove(0);
						new GameSession(p1, p2).start();
					} else {
						send("WAIT_FOR_PLAYERS");
					}
				}

			} catch (Exception e) {
			}
		}
	}

	class GameSession extends Thread {
		ClientThread p1, p2;
		int turn = 0;

		GameSession(ClientThread p1, ClientThread p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		public void run() {
			try {
				p1.send("YOUR_TURN");
				p2.send("WAIT");

				while (true) {
					ClientThread current;
					ClientThread other;
					if (turn == 0) {
						current = p1;
						other = p2;
					} else {
						current = p2;
						other = p1;
					}


					String move = current.receive();
					if (move.contains("CHAT ")) {
						String message = move.substring(5);
						current.send("CHAT " + message);
						other.send("CHAT " + message);
					}else {
						p1.send("MOVE " + move);
						p2.send("MOVE " + move);

						turn = (turn + 1) % 2;
						current.send("WAIT");
						other.send("YOUR_TURN");
					}
				}
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}
