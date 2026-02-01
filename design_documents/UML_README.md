```mermaid
classDiagram
    direction TB

    class Application {
        <<JavaFX>>
    }

    class Thread {
        <<Java>>
    }

    class Client {
        ~ socketClient : Socket
        ~ host : String
        ~ port : int
        ~ out : ObjectOutputStream
        ~ in : ObjectInputStream
        - callback : Consumer~Serializable~
        + run() void
        + send(data : String) void
        ~ Client(call : Consumer~Serializable~, host : String, port : int)
    }

    class Server {
        ~ count : int
        ~ clientArray : ArrayList~ClientThread~
        ~ server : TheServer
        - callback : Consumer~Serializable~
        ~ wordLists : HashMap~String, ArrayList~String~~
        ~ clientsDisconnected : ArrayList~Boolean~
        ~ portNumber : int
        ~ Server(call : Consumer~Serializable~, input : int)
    }

    class TheServer {
        <<inner class>>
        + run() void
    }

    class ClientThread {
        <<inner class>>
        ~ connection : Socket
        ~ count : int
        ~ letterGuessesLeft : int
        ~ currentCategory : int
        ~ wordsWrong : int
        ~ in : ObjectInputStream
        ~ out : ObjectOutputStream
        ~ currentWord : String
        ~ rand : Random
        ~ lettersGuessed : ArrayList~Boolean~
        ~ wordGuessedInCat : ArrayList~Boolean~
        ~ disconnected : boolean
        ~ wordsUsed : ArrayList~String~
        ~ attemptsPerCategory : HashMap~Integer, Integer~
        ~ ClientThread(s : Socket, count : int)
        + run() void
        + wordGuessed() boolean
        + gameWin() boolean
        + categoryActions(input : String) void
    }

    class ServerApp {
        ~ stage : Stage
        ~ startServer : Button
        ~ portInput : TextField
        ~ portText : Text
        ~ log : ListView~String~
        ~ serverConnection : Server
        ~ portNumber : int
        + main(args : String[]) void$
        + start(primaryStage : Stage) void
        + createStartScene() Scene
        + createLogScene() Scene
    }

    class ClientApp {
        ~ stage : Stage
        ~ clientConnection : Client
        ~ attemptsPerCategory : HashMap~Integer, Integer~
        ~ listViewHistory : ListView~Object~
        ~ portInput : TextField
        ~ hostInput : TextField
        ~ guessInput : TextField
        ~ connectButton : Button
        ~ sendButton : Button
        ~ playAgain : Button
        ~ title : Text
        ~ hostText : Text
        ~ portText : Text
        ~ pickACategory : Text
        ~ uniqueCategoriesWon : Text
        ~ wrongAttemptsRemaining : Text
        ~ cat1 : Button
        ~ cat2 : Button
        ~ cat3 : Button
        ~ howToPlay : Button
        ~ quit : Button
        ~ returnButton : Button
        ~ selectedCategory : int
        ~ dataHistory : ArrayList~String~
        - pauseFiveSeconds : PauseTransition
        ~ afterGameText : Text
        ~ serverResponse : String «volatile»
        ~ isWinner : Boolean
        ~ guessesMade : ArrayList~String~
        ~ gameOver : boolean
        ~ roundOver : boolean
        + main(args : String[]) void$
        + start(primaryStage : Stage) void
        + createMainMenuScene() Scene
        + createCategoryScene() Scene
        + createHowToPlayScene() Scene
        + createGameScene() Scene
        + createWinScene() Scene
    }

    Thread <|-- Client : extends
    Thread <|-- TheServer : extends
    Thread <|-- ClientThread : extends
    Application <|-- ServerApp : extends
    Application <|-- ClientApp : extends

    Server *-- TheServer : inner class
    Server *-- ClientThread : inner class

    ServerApp *-- Server : serverConnection
    ClientApp *-- Client : clientConnection
```
