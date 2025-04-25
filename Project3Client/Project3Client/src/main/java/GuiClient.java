import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.Button;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GuiClient extends Application{


	public static void main(String[] args) {
//		Client clientThread = new Client();
//		clientThread.start();
//		Scanner s = new Scanner(System.in);
//		while (s.hasNext()){
//			String x = s.next();
//			clientThread.send(x);
//		}
		launch(args);
	}
	boolean vsBot = false;
	int board[][] = new int[6][7];
	Circle[][] boardCircles = new Circle[6][7]; // 0 will represent empty so we got a 6x7 with 0's
	Button c1,c2,c3,c4,c5,c6,c7;
	TextArea chatLog;
	Text turn;
	Button returnButton;


	@Override
	public void start(Stage primaryStage) throws Exception {
		// setup the background with a blue color
		BorderPane background = new BorderPane();
		background.setStyle("-fx-background-color: #373C3F;");

		GridPane gameBoard = new GridPane();
		gameBoard.setStyle("-fx-background-color: #2E2E2E; -fx-background-radius: 20; -fx-padding: 20;");
		gameBoard.setMaxWidth(600);
		gameBoard.setPadding(new Insets(20, 20, 20, 20)); // top, right, bottom, left
		DropShadow shadow = new DropShadow();
		shadow.setRadius(10);
		shadow.setOffsetX(5);
		shadow.setOffsetY(5);
		shadow.setColor(Color.color(0, 0, 0, 0.5)); // semi-transparent black

		gameBoard.setEffect(shadow);
		gameBoard.setHgap(15);
		gameBoard.setVgap(15);
		for (int r = 0; r < 6; r++){ // makes 6 rows for gameboard
			for (int c = 0; c < 7; c++){ // makes 6 cols for gameboard
				Circle circle = new Circle(25);
				circle.setFill(Color.WHITE);
				circle.setStroke(Color.BLACK);
				gameBoard.add(circle, c, r); // we use grid pane since it allows placement like a grid
				boardCircles[r][c] = circle; // i am saving ref here
			}
		}
		gameBoard.setAlignment(Pos.CENTER);
		// end of gameboard
		chatLog = new TextArea(); // allows us to append messages to best solution
		chatLog.setEditable(false);
		chatLog.setWrapText(true);
		chatLog.setPrefHeight(200);
		Client clientThread = new Client(chatLog);
		clientThread.start();

		TextField chatInput = new TextField();
		chatInput.setPromptText("Type your message...");
		chatInput.setOnAction(e -> {
			String message = chatInput.getText();
			if (!message.isEmpty()) {
				chatLog.appendText("You: " + message + "\n");
				chatInput.clear();
				clientThread.send(message); // send the msg to srvr so i can log it
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
		c1.setStyle("-fx-font-weight: bold;");
		c2.setStyle("-fx-font-weight: bold;");
		c3.setStyle("-fx-font-weight: bold;");
		c4.setStyle("-fx-font-weight: bold;");
		c5.setStyle("-fx-font-weight: bold;");
		c6.setStyle("-fx-font-weight: bold;");
		c7.setStyle("-fx-font-weight: bold;");

		c1.setOnAction(e -> dropPiece(0)); // parameter is the col u wanna drop to.
		c2.setOnAction(e -> dropPiece(1));
		c3.setOnAction(e -> dropPiece(2));
		c4.setOnAction(e -> dropPiece(3));
		c5.setOnAction(e -> dropPiece(4));
		c6.setOnAction(e -> dropPiece(5));
		c7.setOnAction(e -> dropPiece(6));

		Image buttonHoverImg = new Image(getClass().getResource("/click.png").toString());
		ImageView buttonView = new ImageView(buttonHoverImg);
		buttonView.setFitWidth(50);
		buttonView.setFitHeight(50);
		buttonView.setVisible(false);

		ImageView buttonView2 = new ImageView(buttonHoverImg);
		buttonView2.setFitWidth(50);
		buttonView2.setFitHeight(50);
		buttonView2.setVisible(false);

		ImageView buttonView3 = new ImageView(buttonHoverImg);
		buttonView3.setFitWidth(50);
		buttonView3.setFitHeight(50);
		buttonView3.setVisible(false);

		ImageView buttonView4 = new ImageView(buttonHoverImg);
		buttonView4.setFitWidth(50);
		buttonView4.setFitHeight(50);
		buttonView4.setVisible(false);

		ImageView buttonView5 = new ImageView(buttonHoverImg);
		buttonView5.setFitWidth(50);
		buttonView5.setFitHeight(50);
		buttonView5.setVisible(false);

		ImageView buttonView6 = new ImageView(buttonHoverImg);
		buttonView6.setFitWidth(50);
		buttonView6.setFitHeight(50);
		buttonView6.setVisible(false);

		ImageView buttonView7 = new ImageView(buttonHoverImg);
		buttonView7.setFitWidth(50);
		buttonView7.setFitHeight(50);
		buttonView7.setVisible(false);

		VBox aboveImgHolder = new VBox(5,buttonView, c1);
		VBox aboveImgHolder2 = new VBox(5,buttonView2, c2);
		VBox aboveImgHolder3 = new VBox(5,buttonView3, c3);
		VBox aboveImgHolder4 = new VBox(5,buttonView4, c4);
		VBox aboveImgHolder5 = new VBox(5,buttonView5, c5);
		VBox aboveImgHolder6 = new VBox(5,buttonView6, c6);
		VBox aboveImgHolder7 = new VBox(5,buttonView7, c7);

		c1.setOnMouseEntered(e -> buttonView.setVisible(true));
		c1.setOnMouseExited(e -> buttonView.setVisible(false));
		c2.setOnMouseEntered(e -> buttonView2.setVisible(true));
		c2.setOnMouseExited(e -> buttonView2.setVisible(false));
		c3.setOnMouseEntered(e -> buttonView3.setVisible(true));
		c3.setOnMouseExited(e -> buttonView3.setVisible(false));
		c4.setOnMouseEntered(e -> buttonView4.setVisible(true));
		c4.setOnMouseExited(e -> buttonView4.setVisible(false));
		c5.setOnMouseEntered(e -> buttonView5.setVisible(true));
		c5.setOnMouseExited(e -> buttonView5.setVisible(false));
		c6.setOnMouseEntered(e -> buttonView6.setVisible(true));
		c6.setOnMouseExited(e -> buttonView6.setVisible(false));
		c7.setOnMouseEntered(e -> buttonView7.setVisible(true));
		c7.setOnMouseExited(e -> buttonView7.setVisible(false));

		HBox buttonLayout = new HBox(15, aboveImgHolder,aboveImgHolder2,aboveImgHolder3,aboveImgHolder4,aboveImgHolder5,aboveImgHolder6,aboveImgHolder7);


		buttonLayout.setAlignment(Pos.CENTER);

		VBox pageLayout = new VBox(5,buttonLayout, gameBoard);


		pageLayout.setAlignment(Pos.CENTER);
		// end of page layout

		HBox boardAndChat = new HBox(20,pageLayout,chatBox);
		boardAndChat.setAlignment(Pos.CENTER);

		Text title = new Text("Bot vs Player");
		title.setFill(Color.WHITE);
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		title.setStroke(Color.BLACK); // Outline color

		turn = new Text("Deciding turn...");
		turn.setFill(Color.RED);
		turn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		turn.setStroke(Color.BLACK); // Outline color


		returnButton = new Button("Return to Menu");
		returnButton.setVisible(false); // start hidden
		VBox headerAndTurns = new VBox(20,title,turn,returnButton,boardAndChat);
		headerAndTurns.setAlignment(Pos.CENTER);
		headerAndTurns.setPadding(new Insets(0, 0, 90,0)); // top, right, bottom, left
		headerAndTurns.setMargin(turn,new Insets(-20,0,0,0));

		background.setCenter(headerAndTurns);
		Scene gameScene = new Scene(background, 800, 600);
		// end of gameScene


		//start of selection page
		BorderPane selectionBackground = new BorderPane();
		selectionBackground.setStyle("-fx-background-color: #373C3F;");
		Text selectionTitle = new Text("Choose your gamemode!");
		Button aiPlayer = new Button("Bot vs Player");
		Button lanPlay = new Button("Player vs Player");
		Button howPlay = new Button("How to play?");
		VBox selectionLayout = new VBox(20, selectionTitle,aiPlayer,lanPlay,howPlay);  // 20 is the spacing between elements
		selectionLayout.setAlignment(Pos.CENTER);
		selectionBackground.setCenter(selectionLayout);
		Scene selectionScene = new Scene(selectionBackground, 800, 600);

		returnButton.setOnAction(e -> {
			primaryStage.setScene(selectionScene);
			primaryStage.setTitle("Selection Menu");
		});


		aiPlayer.setOnAction(e -> {
			vsBot = true;
			primaryStage.setScene(gameScene);
			primaryStage.setTitle("Connect 4: Bot vs Player");
			primaryStage.show();
		});

		lanPlay.setOnAction(e -> {
			vsBot = false;
			primaryStage.setScene(gameScene);
			primaryStage.setTitle("Connect 4: LAN Multiplayer");
			primaryStage.show();
		});




		// Start of login
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

			primaryStage.setScene(selectionScene);  // Switch to the game scene
			primaryStage.setTitle("Selection Menu");
			primaryStage.show();
		});


		VBox menuLayout = new VBox(20, loginTitle, userNameInput, login);  // 20 is the spacing between elements
		menuLayout.setAlignment(Pos.CENTER);    // Center the button both horizontally and vertically

		menuBackground.setCenter(menuLayout);

		Scene menuScene = new Scene(menuBackground, 800, 600);
		primaryStage.setScene(menuScene);
		primaryStage.setTitle("Game Menu");
		primaryStage.show();
	}

	void disableButtons(){
		c1.setDisable(true);
		c2.setDisable(true);
		c3.setDisable(true);
		c4.setDisable(true);
		c5.setDisable(true);
		c6.setDisable(true);
		c7.setDisable(true);
	}

	//start of game logic
	boolean checkForWin(int player){
		// checking horizontally for win
		for (int row = 0; row < 6; row++){
			for (int col = 0; col < 4; col++){
				if (board[row][col] == player && board[row][col+1] == player && board[row][col+2] == player && board[row][col+3] == player){
					return true;
				}
			}
		}

		for (int row = 0; row < 3; row++){
			for (int col = 0; col < 7; col++){
				if (board[row][col] == player && board[row+1][col] == player && board[row+2][col] == player && board[row+3][col] == player){
					return true;
				}
			}
		}
		return false;
	}


	int findEmptyRow(int col) {
		for (int row = 5; row >= 0; row--) {
			if (board[row][col] == 0) { // we find tha empty row in the col we put in 0 represents empty
				return row; // return where row was empty
			}
		}
		return -1;
	}
	void dropPiece(int col){
		if (vsBot != true){ // if it is not a bot game we do not wanna be here bro
			return;
		}
		int row = findEmptyRow(col);
		if (row == -1){

			chatLog.appendText("Spot is full..!");
			return;
		}

		board[row][col] = 1; // 1 represents player piece
		boardCircles[row][col].setFill(Color.RED);
		if (checkForWin(1)){
			turn.setText("You won!");
			returnButton.setVisible(true);
			disableButtons();
			return;
		}
		turn.setText("Bot's Turn");

		Random r = new Random();

		int botCol = r.nextInt(7);
		int botRow = findEmptyRow(botCol);

		while (botRow == -1){
			botCol = r.nextInt(7);
			botRow = findEmptyRow(botCol);
		}
		board[botRow][botCol] = 2; // 2 represents bot piece this will help me in check win
		boardCircles[botRow][botCol].setFill(Color.YELLOW);
		if (checkForWin(2)){
			returnButton.setVisible(true);
			turn.setText("Bot won!");
			return;
		}
		turn.setText("Your Turn");
	}
}
