
/*
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * Kenorthuy Nguyen
 * kdn683
 * <5-digit Unique No.>
 * Fall 2020
 */

import com.google.gson.Gson;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.stage.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import javafx.scene.*;
import javafx.scene.layout.*;

public class Client extends Application {
    // I/O streams
    PrintWriter toServer = null;
    BufferedReader fromServer = null;
    ClientController controller;
    IncomingReader in;
    OutgoingWriter out;
    Message outgoingMessage;
    Socket clientSocket;
    Gson gson = new Gson();
    Scanner input = new Scanner(System.in);
    CustomerInfo customerInfo;
    List<Item> itemList;
    List<Item> soldItems;


    /**
     * start
     * @param primaryStage
     * @throws IOException
     * sets up javaFX application, handles any IO errors (server isn't on)
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            connectToServer();
            System.out.println(new File(".").getCanonicalPath());
            File file = new File("clientGUI.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
//            Parent root = fxmlLoader.load(getClass().getResource(file).openStream());
            Parent root = fxmlLoader.load(getClass().getResource("/clientGUI.fxml").openStream());
            controller = fxmlLoader.getController();
            controller.myClient = this;
            controller.primaryStage = primaryStage;
            primaryStage.setTitle("Client");
            Scene scene = new Scene(root,1600, 760);
            controller.mainScene = scene;
            controller.mainController = controller;
            primaryStage.setOnCloseRequest(event -> {
                quit();
            });
            primaryStage.setScene(scene);
            primaryStage.show();

        }
        catch (IOException ex) {
            ex.printStackTrace();
            StackPane errorPane = new StackPane();
            Label errorMessage = new Label("Error:\nConnection to server failed.\nPlease start the server and try again.");
            errorMessage.setWrapText(true);
            errorPane.getChildren().add(errorMessage);
            primaryStage.setTitle("Error");
            Scene scene = new Scene(errorPane, 200,100);
            primaryStage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            primaryStage.setScene(scene);
            primaryStage.show();

        }
    }

    //quit
    public void quit() {
        sendToServer(new Message("QUIT"));
        in.stop();
        out.stop();
        System.exit(0);
    }

    /**
     * connectToServer
     * @throws IOException
     * connects to the server VIA socket connection
     */
    void connectToServer() throws IOException {
        clientSocket = new Socket("localhost", 8000);
        // Create an input stream to receive data from the server
        fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        toServer = new PrintWriter(clientSocket.getOutputStream());
        in = new IncomingReader();
        out = new OutgoingWriter();
        in.start();
        out.start();
        System.out.println("connected to server");
    }


    /**
     * IncomingReader class
     * sole purspose is to receive messages from the server and respond accordingly
     */
    class IncomingReader extends Thread {
        public IncomingReader() {
        }
        @Override
        public void run() {
            String incomingString;
            try {
                while(true){
                    if((incomingString = fromServer.readLine()) != null) {
                    Message message = gson.fromJson(incomingString, Message.class);
                    System.out.println("receiving message from server ("+ clientSocket.toString()+ "): " + message );
                    processMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * processMessage
     * @param input
     * takes a serialized message object and interprets what the message is requesting
     */
    protected void processMessage(Message input) {
        try {
            String temp = "";
            switch (input.getType()) {
                case "USER ALREADY EXISTS":
                    controller.loggedIn = false;
                    break;
                case "BADLOGIN":
                    controller.loggedIn = false;
                    break;
                case "LOGINOK":
                    controller.loggedIn = true;
                    this.customerInfo = input.getUser();
                    break;

                case "SIMPLEMESSAGE":
                    System.out.println("received a simple message from server: " + clientSocket);
                    System.out.println(input.getMessage());
                    break;
                case "UPDATE":
                    itemList = input.getItemList();
                    soldItems = input.getSoldItems();
                    controller.setItems(itemList);
                    controller.setSoldItems(soldItems);
                    controller.updateItemsList();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * OutgoingWriter
     * thread used to send messages to the server
     */
    class OutgoingWriter extends Thread {
        @Override
        public void run() {
            while(true){
                String in = input.nextLine();
                outgoingMessage = new Message(in);
                toServer.println(gson.toJson(outgoingMessage));
                toServer.flush();

            }
        }
    }

    protected void sendToServer(Message out){
        toServer.println(gson.toJson(out));
        toServer.flush();
    }

    public static void main(String[] args) {
        launch(args);
    }


}