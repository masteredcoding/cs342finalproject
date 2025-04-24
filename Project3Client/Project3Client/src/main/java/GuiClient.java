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
			}
		}
		gameBoard.setAlignment(Pos.CENTER);
		// end of gameboard
		TextArea chatLog = new TextArea(); // allows us to append messages to best solution
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

		Button c1,c2,c3,c4,c5,c6,c7;
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

		Text title = new Text("Player vs Player");
		title.setFill(Color.WHITE);
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		title.setStroke(Color.BLACK); // Outline color

		Text turn = new Text("Your Turn");
		turn.setFill(Color.RED);
		turn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		turn.setStroke(Color.BLACK); // Outline color

		VBox headerAndTurns = new VBox(20,title,turn,boardAndChat);
		headerAndTurns.setAlignment(Pos.CENTER);
		headerAndTurns.setPadding(new Insets(0, 0, 90,0)); // top, right, bottom, left
		headerAndTurns.setMargin(turn,new Insets(-20,0,0,0));

		background.setCenter(headerAndTurns);
		Scene gameScene = new Scene(background, 800, 600);
		// end of gameScene

		// Start of menu
		BorderPane menuBackground = new BorderPane();
		menuBackground.setStyle("-fx-background-color: #373C3F;");
		Text loginTitle = new Text("Login");
		TextField userNameInput = new TextField();
		userNameInput.setPromptText("Enter unique username...");
		userNameInput.setMaxWidth(150);


		Button login = new Button("Login");

		login.setOnAction(e -> {
			String userName = userNameInput.getText();
			clientThread.send(userName);

			primaryStage.setScene(gameScene);  // Switch to the game scene
			primaryStage.setTitle("Client Chat");
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
}
