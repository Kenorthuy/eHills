import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

class ClientHandler extends PrintWriter implements Runnable, Observer {
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;

    Socket clientSocket;

    protected ClientHandler(Server server, Socket clientSocket) throws IOException {
        super(clientSocket.getOutputStream());
        this.server = server;
        this.clientSocket = clientSocket;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(this.clientSocket.getOutputStream());
    }

    protected void sendToClient(String string) {
        System.out.println("Sending to client: " + string);
        writer.println(string);
        writer.flush();
    }

    public void run() {
        String message;
        System.out.println("client handler Run triggered from client: " + clientSocket.toString() );
        try {
            while(true){
                if((message = reader.readLine()) != null) {
                    System.out.println("Receiving from client: " +message);
                        server.processMessage(message, clientSocket);
                }
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        sendToClient((String) arg);
    }
} // end of class ClientHandlerf class ClientHandler