import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<>();
	TheServer server;
	private List<String> usernames = new ArrayList<>();
	private HashMap<String, ArrayList<ClientThread>> rooms = new HashMap<>();
	private HashMap<ClientThread, String> clientToRoom = new HashMap<>();
	private HashMap<String, Integer> roomTurnIndex = new HashMap<>();

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
					clients.add(c);
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

		public void run() {
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);

				username = (String) in.readObject();

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

				while (true) {
					String data = (String) in.readObject();
					System.out.println(username + " sent: " + data);

					if (data.startsWith("CREATE_ROOM ")) {
						String roomName = data.substring(12);
						createRoom(roomName);
					}
					else if (data.startsWith("JOIN_ROOM ")) {
						String roomName = data.substring(10);
						joinRoom(roomName);
					}
					else if (data.startsWith("MOVE ")) {
						handleMove(data);
					}
					else if (data.equals("REQUEST_ROOMS")) {
						sendRoomList();
					}
					else {
						handleMessage(data);
					}
				}

			} catch (Exception e) {
				System.err.println("OOOPS...Something wrong with client #" + count);
				removeClient();
				e.printStackTrace();
			}
		}

		private void createRoom(String roomName) {
			synchronized (rooms) {
				if (!rooms.containsKey(roomName)) {
					ArrayList<ClientThread> roomClients = new ArrayList<>();
					roomClients.add(this);
					rooms.put(roomName, roomClients);
					clientToRoom.put(this, roomName);
					roomTurnIndex.put(roomName, 0);
					send("WAIT_FOR_PLAYERS");
					System.out.println(username + " created room: " + roomName);
				}
			}
		}

		private void joinRoom(String roomName) {
			synchronized (rooms) {
				if (rooms.containsKey(roomName)) {
					ArrayList<ClientThread> roomClients = rooms.get(roomName);

					roomClients.add(this);
					clientToRoom.put(this, roomName);
					System.out.println(username + " joined room: " + roomName);

					if (roomClients.size() == 2) {
						int index = roomTurnIndex.get(roomName);
						roomClients.get(index).send("YOUR_TURN");
						for (int i = 0; i < roomClients.size(); i++) {
							if (i != index) {
								roomClients.get(i).send("WAIT");
							}
						}
					} else {
						send("WAIT_FOR_PLAYERS"); // still waiting for second player
					}

				} else {
					send("JOIN_FAILED"); // if room doesn't exist
				}
			}
		}


		private void handleMove(String move) {
			String roomName = clientToRoom.get(this);
			if (roomName == null) return;

			synchronized (rooms) {
				ArrayList<ClientThread> roomClients = rooms.get(roomName);
				int index = roomTurnIndex.get(roomName);

				if (roomClients.get(index) == this) {
					// send move to opponent
					for (ClientThread c : roomClients) {
						if (c != this) {
							c.send(move);
						}
					}
					// change turn
					index = (index + 1) % roomClients.size();
					roomTurnIndex.put(roomName, index);

					roomClients.get(index).send("YOUR_TURN");
					for (int i = 0; i < roomClients.size(); i++) {
						if (i != index) {
							roomClients.get(i).send("WAIT");
						}
					}
				} else {
					send("WAIT_YOUR_TURN");
				}
			}
		}

		private void handleMessage(String message) {
			String roomName = clientToRoom.get(this);
			if (roomName == null) return;

			synchronized (rooms) {
				for (ClientThread c : rooms.get(roomName)) {
					if (c != this) {
						c.send(username + ": " + message);
					}
				}
			}
		}

		private void removeClient() {
			try {
				String roomName = clientToRoom.get(this);
				if (roomName != null && rooms.containsKey(roomName)) {
					ArrayList<ClientThread> roomClients = rooms.get(roomName);
					roomClients.remove(this);

					// 💬 If an opponent is still there, tell them you disconnected
					if (!roomClients.isEmpty()) {
						ClientThread opponent = roomClients.get(0);
						opponent.send("OPPONENT_DISCONNECTED");
					} else {
						// If room is empty, remove room completely
						rooms.remove(roomName);
						roomTurnIndex.remove(roomName);
					}
				}
				clientToRoom.remove(this);
				usernames.remove(username);
				clients.remove(this);
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		private void sendRoomList() {
			try {
				synchronized (rooms) {
					StringBuilder sb = new StringBuilder();
					boolean found = false;
					for (String roomName : rooms.keySet()) {
						if (rooms.get(roomName).size() < 2) { // 🔥 only rooms with 1 player
							sb.append(roomName).append(",");
							found = true;
						}
					}
					if (found) {
						send("ROOM_LIST " + sb.toString());
					} else {
						send("ROOM_LIST EMPTY");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
