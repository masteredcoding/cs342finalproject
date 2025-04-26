import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

public class GuiClient extends Application {

	boolean vsBot = false;
	int board[][] = new int[6][7];
	Circle[][] boardCircles = new Circle[6][7];
	Button c1, c2, c3, c4, c5, c6, c7;
	TextArea chatLog;
	Text turn;
	Button returnButton;
	Client clientThread;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane background = new BorderPane();
		background.setStyle("-fx-background-color: #373C3F;");

		GridPane gameBoard = new GridPane();
		gameBoard.setStyle("-fx-background-color: #2E2E2E; -fx-background-radius: 20; -fx-padding: 20;");
		gameBoard.setMaxWidth(600);
		gameBoard.setPadding(new Insets(20));
		DropShadow shadow = new DropShadow();
		shadow.setRadius(10);
		shadow.setOffsetX(5);
		shadow.setOffsetY(5);
		shadow.setColor(Color.color(0, 0, 0, 0.5));
		gameBoard.setEffect(shadow);
		gameBoard.setHgap(15);
		gameBoard.setVgap(15);

		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				Circle circle = new Circle(25);
				circle.setFill(Color.WHITE);
				circle.setStroke(Color.BLACK);
				gameBoard.add(circle, c, r);
				boardCircles[r][c] = circle;
			}
		}
		gameBoard.setAlignment(Pos.CENTER);

		chatLog = new TextArea();
		chatLog.setEditable(false);
		chatLog.setWrapText(true);
		chatLog.setPrefHeight(200);

		clientThread = new Client(chatLog);
		clientThread.setGuiClient(this); // NEW
		clientThread.start();

		TextField chatInput = new TextField();
		chatInput.setPromptText("Type your message...");
		chatInput.setOnAction(e -> {
			String message = chatInput.getText();
			if (!message.isEmpty()) {
				chatLog.appendText("You: " + message + "\n");
				chatInput.clear();
				clientThread.send(message);
			}
		});

		VBox chatBox = new VBox(10, chatLog, chatInput);
		chatBox.setAlignment(Pos.CENTER);
		chatBox.setMaxWidth(200);

		c1 = new Button("DROP");
		c2 = new Button("DROP");
		c3 = new Button("DROP");
		c4 = new Button("DROP");
		c5 = new Button("DROP");
		c6 = new Button("DROP");
		c7 = new Button("DROP");
		Button[] buttons = {c1, c2, c3, c4, c5, c6, c7};
		for (int i = 0; i < buttons.length; i++) {
			int col = i;
			buttons[i].setStyle("-fx-font-weight: bold;");
			buttons[i].setOnAction(e -> dropPiece(col));
		}

		HBox buttonLayout = new HBox(15, c1, c2, c3, c4, c5, c6, c7);
		buttonLayout.setAlignment(Pos.CENTER);

		VBox pageLayout = new VBox(5, buttonLayout, gameBoard);
		pageLayout.setAlignment(Pos.CENTER);

		HBox boardAndChat = new HBox(20, pageLayout, chatBox);
		boardAndChat.setAlignment(Pos.CENTER);

		Text title = new Text("Bot vs Player");
		title.setFill(Color.WHITE);
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		title.setStroke(Color.BLACK);

		turn = new Text("Deciding turn...");
		turn.setFill(Color.RED);
		turn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		turn.setStroke(Color.BLACK);

		returnButton = new Button("Return to Menu");
		returnButton.setVisible(false);

		VBox headerAndTurns = new VBox(20, title, turn, returnButton, boardAndChat);
		headerAndTurns.setAlignment(Pos.CENTER);
		headerAndTurns.setPadding(new Insets(0, 0, 90, 0));

		background.setCenter(headerAndTurns);
		Scene gameScene = new Scene(background, 800, 600);

		BorderPane selectionBackground = new BorderPane();
		selectionBackground.setStyle("-fx-background-color: #373C3F;");
		Text selectionTitle = new Text("Choose your gamemode!");
		Button aiPlayer = new Button("Bot vs Player");
		Button lanPlay = new Button("Player vs Player");
		Button howPlay = new Button("How to play?");
		VBox selectionLayout = new VBox(20, selectionTitle, aiPlayer, lanPlay, howPlay);
		selectionLayout.setAlignment(Pos.CENTER);
		selectionBackground.setCenter(selectionLayout);
		Scene selectionScene = new Scene(selectionBackground, 800, 600);

		BorderPane menuBackground = new BorderPane();
		menuBackground.setStyle("-fx-background-color: #373C3F;");
		Text loginTitle = new Text("Hello, create a username!");
		TextField userNameInput = new TextField();
		userNameInput.setPromptText("Enter unique username...");
		userNameInput.setMaxWidth(150);
		Button login = new Button("Begin");

		login.setOnAction(e -> {
			String userName = userNameInput.getText();
			clientThread.send(userName);
			primaryStage.setScene(selectionScene);
			primaryStage.setTitle("Selection Menu");
			primaryStage.show();
		});

		VBox menuLayout = new VBox(20, loginTitle, userNameInput, login);
		menuLayout.setAlignment(Pos.CENTER);
		menuBackground.setCenter(menuLayout);
		Scene menuScene = new Scene(menuBackground, 800, 600);

		primaryStage.setScene(menuScene);
		primaryStage.setTitle("Game Menu");
		primaryStage.show();

		aiPlayer.setOnAction(e -> {
			vsBot = true;
			primaryStage.setScene(gameScene);
			title.setText("Connect 4: Bot vs Player");
			primaryStage.show();
		});

		lanPlay.setOnAction(e -> {
			vsBot = false;
			primaryStage.setScene(gameScene);
			title.setText("Connect 4: LAN Multiplayer");
			primaryStage.show();
			setupMoveListener();
		});

		returnButton.setOnAction(e -> {
			resetGame();
			primaryStage.setScene(selectionScene);
			primaryStage.setTitle("Selection Menu");
		});
	}

	void setupMoveListener() {
		// start listening for move updates
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(100);
					if (clientThread != null && clientThread.isMyTurn()) {
						Platform.runLater(() -> turn.setText("Your Turn!"));
					} else {
						Platform.runLater(() -> turn.setText("Opponent's Turn"));
					}
				} catch (Exception e) {
					break;
				}
			}
		}).start();
	}

	void disableButtons() {
		c1.setDisable(true);
		c2.setDisable(true);
		c3.setDisable(true);
		c4.setDisable(true);
		c5.setDisable(true);
		c6.setDisable(true);
		c7.setDisable(true);
	}

	boolean checkForWin(int player) {
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 4; col++) {
				if (board[row][col] == player && board[row][col+1] == player && board[row][col+2] == player && board[row][col+3] == player) {
					return true;
				}
			}
		}
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 7; col++) {
				if (board[row][col] == player && board[row+1][col] == player && board[row+2][col] == player && board[row+3][col] == player) {
					return true;
				}
			}
		}
		return false;
	}

	void resetGame() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				board[r][c] = 0;
				boardCircles[r][c].setFill(Color.WHITE);
			}
		}
		turn.setText("Your Turn!");
		c1.setDisable(false);
		c2.setDisable(false);
		c3.setDisable(false);
		c4.setDisable(false);
		c5.setDisable(false);
		c6.setDisable(false);
		c7.setDisable(false);
		returnButton.setVisible(false);
	}

	int findEmptyRow(int col) {
		for (int row = 5; row >= 0; row--) {
			if (board[row][col] == 0) {
				return row;
			}
		}
		return -1;
	}

	void dropPiece(int col) {
		if (vsBot) {
			// Bot vs player mode
			int row = findEmptyRow(col);
			if (row == -1) {
				chatLog.appendText("Spot is full..!\n");
				return;
			}
			board[row][col] = 1;
			boardCircles[row][col].setFill(Color.RED);

			if (checkForWin(1)) {
				turn.setText("You won!");
				returnButton.setVisible(true);
				disableButtons();
				return;
			}

			turn.setText("Bot's Turn");

			Random r = new Random();
			int botCol = r.nextInt(7);
			int botRow = findEmptyRow(botCol);
			while (botRow == -1) {
				botCol = r.nextInt(7);
				botRow = findEmptyRow(botCol);
			}
			board[botRow][botCol] = 2;
			boardCircles[botRow][botCol].setFill(Color.YELLOW);

			if (checkForWin(2)) {
				returnButton.setVisible(true);
				turn.setText("Bot won!");
				return;
			}
			turn.setText("Your Turn");
		} else {
			// LAN Multiplayer
			if (!clientThread.isMyTurn()) {
				chatLog.appendText("Not your turn!\n");
				return;
			}

			int row = findEmptyRow(col);
			if (row == -1) {
				chatLog.appendText("Spot is full..!\n");
				return;
			}

			clientThread.send("" + col); // Send the column to servedr
		}
	}
	public void applyMove(int col, boolean isMyMove) {
		int row = findEmptyRow(col);
		if (row == -1) {
			return; // Should not happen if server is correct
		}

		int playerNumber = isMyMove ? 1 : 2; // 1 = you (red), 2 = opponent (yellow)

		board[row][col] = playerNumber;
		if (playerNumber == 1) {
			boardCircles[row][col].setFill(Color.RED);
		} else {
			boardCircles[row][col].setFill(Color.YELLOW);
		}

		// ✅ After applying the move, check if the player who made the move won
		if (checkForWin(playerNumber)) {
			if (isMyMove) {
				showEndGameAlert("You Won!");
			} else {
				showEndGameAlert("You Lost!");
			}
			disableButtons();
			returnButton.setVisible(true);
			return;
		}

		// ✅ If no win, check if board is full (DRAW)
		if (isBoardFull()) {
			showEndGameAlert("It's a Draw!");
			disableButtons();
			returnButton.setVisible(true);
		}
	}
	private void showEndGameAlert(String message) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();

		turn.setText(message); // Also update the label after closing popup
	}


	private boolean isBoardFull() {
		for (int col = 0; col < 7; col++) {
			if (findEmptyRow(col) != -1) {
				return false; // still empty spot found
			}
		}
		return true; // no empty spots, board full
	}


}
