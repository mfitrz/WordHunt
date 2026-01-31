import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;


public class Server {
	int count = 1, portNumber;
	ArrayList<ClientThread> clientArray = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	HashMap<String, ArrayList<String>> wordLists;  // List of words per category
	ArrayList<Boolean> clientsDisconnected = new ArrayList<Boolean>();  // Keeps track of clients that are disconnected

	// Constructor
	Server(Consumer<Serializable> call, int input) {
		portNumber = input;
		callback = call;
		server = new TheServer();
		server.setDaemon(true);
		server.start();

		// Possible Words per Category

		// Animals Category
		ArrayList<String> animals = new ArrayList<String>();
		animals.add("cat");
		animals.add("dog");
		animals.add("monkey");
		animals.add("fox");
		animals.add("wolf");

		// States Category
		ArrayList<String> states = new ArrayList<String>();
		states.add("idaho");
		states.add("maine");
		states.add("wyoming");
		states.add("vermont");
		states.add("florida");

		// Food Category
		ArrayList<String> foods = new ArrayList<String>();
		foods.add("grape");
		foods.add("sandwich");
		foods.add("corn");
		foods.add("orange");
		foods.add("pear");

		wordLists = new HashMap<String, ArrayList<String>>();
		wordLists.put("Animals", animals);
		wordLists.put("Foods", foods);
		wordLists.put("States", states);
	}

	public class TheServer extends Thread{
		public void run() {

			try(ServerSocket mysocket = new ServerSocket(portNumber);) {
				System.out.println(portNumber);

				while(true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("Client #" + count + " has connected to the server!");
					clientArray.add(c);
					clientsDisconnected.add(false);
					c.setDaemon(true);
					c.start();

					count++;

				}
			}
			catch(Exception e) {
				callback.accept("Server socket did not launch.");
			}
		}
	}

	class ClientThread extends Thread{
		Socket connection;
		int count, letterGuessesLeft, currentCategory, wordsWrong = 0;
		ObjectInputStream in;
		ObjectOutputStream out;
		String currentWord;
		Random rand = new Random();
		ArrayList<Boolean> lettersGuessed, wordGuessedInCat;
		boolean disconnected = false;
		ArrayList<String> wordsUsed;
		HashMap<Integer, Integer> attemptsPerCategory = new HashMap<Integer, Integer>();

		// Client Thread Constructor
		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
			letterGuessesLeft = 6;
			wordGuessedInCat = new ArrayList<Boolean>();
			for (int i = 0; i < 3; i++) {
				wordGuessedInCat.add(false);
			}
			wordsUsed = new ArrayList<String>();

			// Initialize attempts per category
			attemptsPerCategory.put(1, 0);
			attemptsPerCategory.put(2, 0);
			attemptsPerCategory.put(3, 0);
		}

		// The run() method is called whenever ClientThread.start() is called
		public void run(){
			try {
				// Save input and output stream to server instance
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			//ClientThread t = clientArray.get(count - 1);

			// Loop while server is running
			while(true) {
				try {

					// Category Choice Received from Client

					String data = in.readObject().toString();
					if (data.equals("Category 1")) {
						categoryActions("Animals");
						currentCategory = 1;
					} else if (data.equals("Category 2")) {
						categoryActions("Foods");
						currentCategory = 2;
					} else if (data.equals("Category 3")) {
						categoryActions("States");
						currentCategory = 3;
					} else {

						// Guess Received from Client

						if (data.length() == 1 && Character.isLetter(data.charAt(0))) {
							if (currentWord.contains(data.toLowerCase())) {
								int indexOfLetter = currentWord.indexOf(data);
								callback.accept("Client " + count + " guessed the letter: " + data + " which is in the word.");
								ClientThread thread = clientArray.get(count - 1);
								lettersGuessed.set(indexOfLetter, true);
								try {
									thread.out.writeObject(data + " is in the word and is located at index " + String.valueOf(indexOfLetter));
								} catch(Exception e) {

								}

								// Check if client guessed the word correctly

								if (wordGuessed()) {
									callback.accept("Client " + count + " has guessed all the letters in the word! THEY WON");
									try {
										thread.out.writeObject("You have guessed all the letters in the word!");
									} catch(Exception e) {

									}
									letterGuessesLeft = 6;
									if (gameWin()) {
										wordsWrong = 0;
										wordGuessedInCat = new ArrayList<Boolean>();
										for (int i = 0; i < 3; i++) {
											wordGuessedInCat.add(false);
										}
										wordsUsed = new ArrayList<String>();
										callback.accept("Client " + count + " has guessed one word in each category!");
										try {
											thread.out.writeObject("You have guessed one word in each category. You win!");
										} catch(Exception e) {

										}
									}
								}
							} else {
								// Client did not guess the letter correctly

								callback.accept("Client " + count + " guessed the letter: " + data + " which is not in the word.");
								ClientThread thread = clientArray.get(count - 1);
								letterGuessesLeft -= 1;
								try {
									thread.out.writeObject(data + " is not in the word. You have " + letterGuessesLeft + " guesses left.");
								} catch(Exception e) {

								}
								if (letterGuessesLeft == 0) {
									letterGuessesLeft = 6;

									// Category is entered, increment the attempt count by 1
									int currentCategoryAttempts = attemptsPerCategory.get(currentCategory);
									attemptsPerCategory.put(currentCategory, currentCategoryAttempts + 1);

									callback.accept("Client " + count + " is out of letter guesses.");
									if (attemptsPerCategory.get(currentCategory) >= 3) {
										// Reset category attempts for next game
										attemptsPerCategory.put(1, 0);
										attemptsPerCategory.put(2, 0);
										attemptsPerCategory.put(3, 0);
										wordGuessedInCat = new ArrayList<Boolean>();
										for (int i = 0; i < 3; i++) {
											wordGuessedInCat.add(false);
										}
										wordsUsed = new ArrayList<String>();
										callback.accept("Client " + count + " has guessed three words incorrectly. GAME OVER");

										try {
											thread.out.writeObject("You are out of guesses. You have also guessed three words wrong already. GAME OVER");
										} catch(Exception e) {

										}
									} else {
										try {
											thread.out.writeObject("You are out of guesses. You can either pick a new category or try again.");
										} catch(Exception e) {

										}
									}
								}
							}
						} else {

							// Server received an invalid input for a guess

							callback.accept("Client " + count + " did not input a single letter and has lost a guess.");
							ClientThread thread = clientArray.get(count - 1);
							letterGuessesLeft -= 1;
							try {
								thread.out.writeObject("Your input was not a single letter. You have " + letterGuessesLeft + " guesses left.");
							} catch(Exception e) {

							}

							// Client is out of letter guesses

							if (letterGuessesLeft == 0) {
								// Category is entered, increment the attempt count by 1
								int currentCategoryAttempts = attemptsPerCategory.get(currentCategory);
								attemptsPerCategory.put(currentCategory, currentCategoryAttempts + 1);
								letterGuessesLeft = 6;
								callback.accept("Client " + count + " is out of letter guesses.");

								// Check if the entire game is over

								if (attemptsPerCategory.get(currentCategory) >= 3) {
									// Reset all game variables for next play through

									attemptsPerCategory.put(1, 0);
									attemptsPerCategory.put(2, 0);
									attemptsPerCategory.put(3, 0);
									callback.accept("Client " + count + " has guessed three words incorrectly. GAME OVER");
									wordGuessedInCat = new ArrayList<Boolean>();
									for (int i = 0; i < 3; i++) {
										wordGuessedInCat.add(false);
									}
									wordsUsed = new ArrayList<String>();
									try {
										thread.out.writeObject("You are out of guesses. You have also guessed three words wrong already. GAME OVER");
									} catch(Exception e) {

									}
								} else {
									// Client still has lives, continue the game

									try {
										thread.out.writeObject("You are out of guesses. You can either pick a new category or try again.");
									} catch(Exception e) {

									}
								}
							}
						}
					}
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					if (!disconnected) {
						callback.accept("Client " + count + " has disconnected from the server.");
						disconnected = true;
						clientsDisconnected.set(count - 1, true);
					} else {
						clientArray.get(count - 1).interrupt();
						try {
							connection.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						boolean temp = false;

						// Checks for clients on the server
						for (int i = 0; i < clientsDisconnected.size(); i++) {
							if (clientsDisconnected.get(i) == false) {
								temp = true;
								break;
							}
						}
						if (temp == false) {
							break;
						}
					}
				}
			}
			callback.accept("There are no more clients on the server. Shutting down server thread.");
		}

		// Checks if the user was able to guess the word in the category
		public boolean wordGuessed() {
			for (int i = 0; i < lettersGuessed.size(); i++) {
				if (lettersGuessed.get(i) == false) {
					return false;
				}
			}

			if (currentCategory == 1) {
				wordGuessedInCat.set(0, true);
			} else if (currentCategory == 2) {
				wordGuessedInCat.set(1, true);
			} else if (currentCategory == 3) {
				wordGuessedInCat.set(2, true);
			}
			return true;
		}

		// Check if the game has been won
		public boolean gameWin() {
			for (int i = 0; i < wordGuessedInCat.size(); i++) {
				if (wordGuessedInCat.get(i) == false) {
					return false;
				}
			}
			return true;
		}

		// Print action to the server log
		public void categoryActions(String input) {
			int temp = 0;
			if (input.equals("Animals")) {
				temp = 1;
			} else if (input.equals("Foods")) {
				temp = 2;
			} else if (input.equals("States")) {
				temp = 3;
			}
			callback.accept("Client " + count + " chose Category " + temp + ": " + input);
			ClientThread thread = clientArray.get(count - 1);
			ArrayList<String> wordList = wordLists.get(input);
			currentWord = wordList.get(rand.nextInt(wordList.size()));

			// Pick a unique random word for the client to guess
			while (wordsUsed.contains(currentWord)) {
				rand = new Random();
				currentWord = wordList.get(rand.nextInt(wordList.size()));
			}
			wordsUsed.add(currentWord);
			callback.accept("Client " + count + " 's word is : " + currentWord);
			lettersGuessed = new ArrayList<Boolean>();

			// Initialize a boolean array that tracks which letters were guessed so far
			for (int i = 0; i < currentWord.length(); i++) {
				lettersGuessed.add(false);
			}

			// Send messages to the client

			try {
				thread.out.writeObject("You have selected Category " + temp + ": " + input);
				thread.out.writeObject("Your word has " + currentWord.length() + " letters.");
			} catch(Exception e) {

			}
		}

	}//end of client thread

}
