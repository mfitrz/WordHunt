# WordHunt

A networked multiplayer word-guessing game built with **JavaFX** and **Java sockets**.
The project features a dedicated **server application** and **client application** that communicate over TCP, supporting multiple simultaneous players.

---

## Overview

WordHunt is a Hangman-style game where players connect to a server and attempt to guess hidden words across three categories: Animals, Foods, and States. Each round, the server selects a random word and the player guesses one letter at a time.

The objective is to:

* Successfully guess one word from each of the three categories
* Stay within 6 letter guesses per word
* Avoid accumulating 3 total failed words, which ends the game

---

## Features

* **Client-Server Architecture**
  + TCP socket communication using Java ObjectStreams
  + Server handles multiple concurrent client connections
  + Each client runs in its own dedicated thread
* **Interactive JavaFX GUI**
  + Server interface for monitoring connections and game activity
  + Client interface with category selection, letter guessing, and game status
  + How-to-play instructions screen
* **Game Logic**
  + Three word categories: Animals, Foods, and States
  + 6 letter guesses per word with real-time feedback
  + Win/loss detection with play-again support
  + Duplicate guess prevention
* **Multithreaded Design**
  + Server spawns a new thread per client connection
  + Client uses a background thread for receiving server messages
  + Thread-safe UI updates via `Platform.runLater()`

---

## Technologies Used

* **Java**
* **JavaFX** (GUI)
* **Maven** (build & dependency management)
* **JUnit** (testing framework)

---

## How to Run

### Prerequisites

* Java **JDK 8+**
* Apache Maven

### Run the Server

From the project root directory:

```
mvn compile exec:java
```

Enter a port number (e.g. `5555`) and click **Start Server**.

### Run the Client

In a separate terminal from the project root directory:

```
mvn compile exec:java -Pclient
```

Enter the host (`127.0.0.1` for local) and the same port number, then click **Connect**.

### Run Tests

```
mvn test
```
