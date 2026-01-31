import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ServerApp extends Application {
	Stage stage;
	Button startServer;
	TextField portInput;
	Text portText;
	ListView<String> log;
	Server serverConnection;
	int portNumber;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		primaryStage.setTitle("WordHunt - Server");

		// Create and store scenes in variables
		Scene startScene = createStartScene();
		Scene logScene = createLogScene();

		/*
		When startServer.setOnAction is called, the server constructor is called.
		In the server constructor, the 'TheServer' constructor is called which establishes the connection between
		the server and the client.
		*/
		startServer.setOnAction(e -> {
			portNumber = Integer.parseInt(portInput.getText());
			stage.setScene(logScene);

			// This method is always called when the client sends data and the server receives it.
			serverConnection = new Server(data -> {
				Platform.runLater(() -> {
					log.getItems().add(data.toString());  // Update the server list view
				});
			}, portNumber);
		});


		// Exit the platform
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(startScene);
		primaryStage.show();
	}

	public Scene createStartScene () {

		portInput = new TextField();

		portText = new Text("Input Port");

		startServer = new Button("Start Server");

		VBox startVBox = new VBox(20, portText, portInput, startServer);
		startVBox.setAlignment(Pos.CENTER);

		BorderPane startPane = new BorderPane();
		startPane.setCenter(startVBox);

		Scene returnScene = new Scene(startPane, 700, 700);

		return returnScene;
	}

	public Scene createLogScene() {

		log = new ListView<String>();

		BorderPane logPane = new BorderPane();
		logPane.setCenter(log);

		Scene returnScene = new Scene(logPane, 700, 700);

		return returnScene;
	}


}
