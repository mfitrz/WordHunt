import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
    Socket socketClient;
    String host;
    int port;

    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<Serializable> callback;

    Client(Consumer<Serializable> call, String host, int port){
        callback = call;
        this.host = host;
        this.port = port;
    }

    public void run() {

        try {
            socketClient= new Socket(host, port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {}

        while(true) {

            try {
                String message = in.readObject().toString();
                callback.accept(message);
            }
            catch(Exception e) {

            }
        }

    }

    public void send(String data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
