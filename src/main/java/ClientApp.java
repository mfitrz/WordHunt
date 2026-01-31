import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ClientApp extends Application {

	Stage stage;
	Client clientConnection;
	HashMap<Integer, Integer> attemptsPerCategory = new HashMap<Integer, Integer>();
	ListView<Object> listViewHistory = new ListView<Object>();
	TextField portInput, hostInput, guessInput;
	Button connectButton;
	Button sendButton, playAgain;

	Text title, hostText, portText, pickACategory, uniqueCategoriesWon, wrongAttemptsRemaining;
	Button cat1, cat2, cat3, howToPlay, quit, returnButton;
	int selectedCategory;
	ArrayList<String> dataHistory = new ArrayList<String>();
	private PauseTransition pauseFiveSeconds = new PauseTransition(Duration.seconds(5));
	Text afterGameText;
	volatile String serverResponse;
	Boolean isWinner = false;

	ArrayList<String> guessesMade = new ArrayList<String>();

	boolean gameOver, roundOver;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		primaryStage.setTitle("WordHunt - Client");

		Scene mainScene = createMainMenuScene();
		Scene catScene = createCategoryScene();
		Scene howToPlayScene = createHowToPlayScene();
		Scene gameScene = createGameScene();
		Scene winScene = createWinScene();

		attemptsPerCategory.put(1, 0);
		attemptsPerCategory.put(2, 0);
		attemptsPerCategory.put(3, 0);

		connectButton.setOnAction(e -> {
			stage.setScene(catScene);
			// Client constructor creates a connection between the client program and the server program.

			clientConnection = new Client(data -> {

				/*
					serverResponse must be set outside Platform.runLater,
					otherwise server response will be behind by 1.
			 	*/
				serverResponse = data.toString();
				Platform.runLater(() -> {
					System.out.println("Response from server received: " + data.toString());
					listViewHistory.getItems().add(data.toString());
					dataHistory.add(data.toString());
				});
			}, hostInput.getText(), Integer.parseInt(portInput.getText()));
			clientConnection.setDaemon(true);
			clientConnection.start();
		});

		cat1.setOnAction(e -> {
			guessInput.setDisable(false);
			sendButton.setDisable(false);
			stage.setScene(gameScene);
			clientConnection.send("Category 1");
			selectedCategory = 1;
		});

		cat2.setOnAction(e -> {
			guessInput.setDisable(false);
			sendButton.setDisable(false);
			stage.setScene(gameScene);
			clientConnection.send("Category 2");
			selectedCategory = 2;
		});

		cat3.setOnAction(e -> {
			guessInput.setDisable(false);
			sendButton.setDisable(false);
			stage.setScene(gameScene);
			clientConnection.send("Category 3");
			selectedCategory = 3;
		});

		howToPlay.setOnAction(e -> {
			stage.setScene(howToPlayScene);
		});

		returnButton.setOnAction(e -> {
			stage.setScene(catScene);
		});

		pauseFiveSeconds.setOnFinished(e -> {
			System.out.println("Inside pauseFiveSeconds.setOnFinished()");
			if (!gameOver) {
				stage.setScene(catScene);
			} else {
				if (isWinner) {
					afterGameText.setText("You won!\nPlay again?");
				} else {
					afterGameText.setText("Game Over\nPlay Again?");
				}

				// Reset all game variables
				serverResponse = "NEW GAME";
				isWinner = false;
				roundOver = false;
				gameOver = false;
				listViewHistory.getItems().clear();
				guessesMade = new ArrayList<>();
				attemptsPerCategory = new HashMap<Integer, Integer>();
				attemptsPerCategory.put(1, 0);
				attemptsPerCategory.put(2, 0);
				attemptsPerCategory.put(3, 0);
				cat1.setDisable(false);
				cat2.setDisable(false);
				cat3.setDisable(false);
				stage.setScene(winScene);
			}

			// Clear the move history box for the next category
			listViewHistory.getItems().clear();
			guessesMade = new ArrayList<>();

			// Enable Guessing
			guessInput.setDisable(false);
			sendButton.setDisable(false);

			System.out.println("Game finished pause.");
		});

		sendButton.setOnAction(e -> {
			System.out.println("Send Button Clicked");
			roundOver = false;
			gameOver = false;

			// Send the user's guess to the server

			if (!guessesMade.contains(guessInput.getText())) {
				guessesMade.add(guessInput.getText());
				clientConnection.send(guessInput.getText());

				// Clear the user's guess
				guessInput.clear();

				// Check if the round is over

				System.out.println("[Before branching: " + serverResponse +"]");

				if (serverResponse.equals("You have guessed all the letters in the word!") ||
						serverResponse.equals("You are out of guesses. You can either pick a new category or try again.") ||
						serverResponse.equals("You have guessed one word in each category. You win!") ||
						serverResponse.equals("You are out of guesses. You have also guessed three words wrong already. GAME OVER"))
				{
					System.out.println("Round over flag set.");

					// If isWinner = true, game over screen text will change
					if (serverResponse.equals("You have guessed one word in each category. You win!")) {
						isWinner = true;
					}

					roundOver = true;
				}

				if (serverResponse.equals("You have guessed all the letters in the word!") ||
						serverResponse.equals("You are out of guesses. You can either pick a new category or try again."))
				{
					System.out.println("if branch: " + serverResponse);
					boolean wonCurrentCategory = false;

					// Check if the selected category was won
					if (serverResponse.equals("You have guessed all the letters in the word!")) {
						wonCurrentCategory = true;
					}

					// Increment current selectedCategory attempt
					int currentCategoryAttempt = attemptsPerCategory.get(selectedCategory);
					attemptsPerCategory.put(selectedCategory, currentCategoryAttempt + 1);

					// If the selected category was won, disable that button
					if (wonCurrentCategory) {
						if (selectedCategory == 1) {
							cat1.setDisable(true);
						} else if (selectedCategory == 2) {
							cat2.setDisable(true);
						} else if (selectedCategory == 3) {
							cat3.setDisable(true);
						}

					}

					// Re-enable all OTHER categories with less than 3 attempts
					if (selectedCategory != 1 && attemptsPerCategory.get(1) <= 3) {
						cat1.setDisable(false);
					}
					if (selectedCategory != 2 && attemptsPerCategory.get(2) <= 3) {
						cat2.setDisable(false);
					}
					if (selectedCategory != 3 && attemptsPerCategory.get(3) <= 3) {
						cat3.setDisable(false);
					}


				} else if (serverResponse.equals("You have guessed one word in each category. You win!") ||
						serverResponse.equals("You are out of guesses. You have also guessed three words wrong already. GAME OVER"))
				{
					System.out.println("else if branch: " + serverResponse);

					// Set game over flag
					gameOver = true;
				}

				// Change scene if the round is over
				if (roundOver) {
					System.out.println("roundOver branch");

					// Disable guesses
					guessInput.setDisable(true);
					sendButton.setDisable(true);

					// Change Scene
					pauseFiveSeconds.play();
				}
			} else {
				guessInput.clear();
				listViewHistory.getItems().add("You have already made that guess, try another one.");
			}
		});  // sendButton.setOnAction

		playAgain.setOnAction(e -> {
			System.out.println("---NEW GAME---");
			stage.setScene(catScene);
		});

		quit.setOnAction(e -> {
			Platform.exit();
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	public Scene createMainMenuScene() throws IOException {
		title = new Text("WordHunt");
		hostText = new Text("Input Host");
		portText = new Text("Input Port");

		connectButton = new Button("Connect");

		hostInput = new TextField();
		hostInput.setText("127.0.0.1");
		hostInput.setPromptText("Default IP: 127.0.0.1");
		portInput = new TextField();

		VBox mainVBox = new VBox(20, title, hostText, hostInput, portText, portInput, connectButton);
		mainVBox.setAlignment(Pos.CENTER);

		BorderPane mainPane = new BorderPane();
		mainPane.setCenter(mainVBox);

		Scene returnScene = new Scene(mainPane, 700, 700);

		return returnScene;
	}

	public Scene createCategoryScene() throws IOException {
		// Present game info to player
		pickACategory = new Text("Pick a Category!");

		// Categories
		cat1 = new Button("Animals");
		cat2 = new Button("Foods");
		cat3 = new Button("States");

		// How to play and quit
		howToPlay = new Button("How to play");
		quit = new Button("Quit");

		VBox catVBox = new VBox(20, pickACategory, cat1, cat2, cat3, howToPlay, quit);
		catVBox.setAlignment(Pos.CENTER);

		BorderPane catPane = new BorderPane();
		catPane.setCenter(catVBox);

		Scene returnScene = new Scene(catPane, 700, 700);

		return returnScene;
	}

	public Scene createHowToPlayScene() throws IOException {
		Text howToPlay = new Text();
		howToPlay.setText(
				"In this game, you must guess one word correctly out of each of the three categories." +
				"If you guess a word wrong a total of three times, you lose the game." +
				"Pick a category, then you must make one letter guesses." +
				"You have six total guesses per category, and if you fail to find all the letters within 6 attempts, you will use up one of the three wrong attempts you are allowed to make." +
				"Guessing a word correctly will disable the category for one round, and will be re-enabled after finishing different category."
		);


		returnButton = new Button("Return");

		VBox howToPlayVBox = new VBox(howToPlay, returnButton);
		howToPlayVBox.setAlignment(Pos.CENTER);



		BorderPane howToPlayPane = new BorderPane();
		howToPlayPane.setCenter(howToPlayVBox);

		howToPlay.wrappingWidthProperty().bind(howToPlayPane.widthProperty()); // Word Wrap Description
		Scene returnScene = new Scene(howToPlayPane, 700, 700);

		return returnScene;
	}

	public Scene createGameScene() throws IOException {
		guessInput = new TextField();
		sendButton = new Button("Guess");

		HBox hBox = new HBox(guessInput, sendButton);
		hBox.setAlignment(Pos.CENTER);

		VBox gameVBox = new VBox(20, listViewHistory, hBox);
		gameVBox.setAlignment(Pos.CENTER);

		BorderPane gamePane = new BorderPane();
		gamePane.setCenter(gameVBox);

		Scene returnScene = new Scene(gamePane, 700, 700);

		return returnScene;
	}

	public Scene createWinScene() throws IOException {
		playAgain = new Button("Play Again");
		afterGameText = new Text();

		VBox winVBox = new VBox(20, afterGameText, playAgain, quit);
		winVBox.setAlignment(Pos.CENTER);

		BorderPane winPane = new BorderPane();
		winPane.setCenter(winVBox);

		Scene returnScene = new Scene(winPane, 700, 700);

		return returnScene;
	}

}
