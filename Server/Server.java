
/*
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * Kenorthuy Nguyen
 * kdn683
 * <5-digit Unique No.>
 * Fall 2020
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.Block;
import org.bson.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

public class Server extends Observable {

    static Server server;
    ServerSocket ss;
    Socket clientSocket;
    PrintWriter writer;
    Gson gson;
    GsonBuilder builder;
    String output;
    Scanner consoleInput = new Scanner(System.in);
    ArrayList<Item> itemsList;
    ArrayList<Item> soldItems;
    mongoDB database;

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        server = new Server();
        server.connectToDatabase();
        server.SetupNetworking();
        System.out.println("server done with setup");
    }

    /**
     * connectToDatabase
     * connects to cloud database (MongoDB)
     */
    private void connectToDatabase() {
        database = new mongoDB();
    }

    /**
     * setupNetworking
     * sets up the server socket and ability to accept clients
     */
    private void SetupNetworking() {
        gson = new Gson();
        builder = new GsonBuilder();
        int port = 8000;
        try {
            ss = new ServerSocket(port);
            while (true) {
                clientSocket = ss.accept();
                ClientHandler handler = new ClientHandler(this, clientSocket);
                Thread t = new Thread(handler);
                t.start();
                addObserver(handler);
                System.out.println("Server: got a connection to client");
            }
        } catch (IOException e) {
        }
    }

    /**
     * processMessage
     * @param input
     * @param clientSocket
     * @throws IOException
     * handles all incoming messages from clients
     */
    protected synchronized void processMessage(String input, Socket clientSocket) throws IOException {
        PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
        Message message = gson.fromJson(input, Message.class);
        try {
            String temp = "";
            switch (message.getType()) {
                case "LOGIN":
                    CustomerInfo loginRequest = database.findUserInfo(message.getUser());
                    if(loginRequest == null){
                        Message failedLogin = new Message("BADLOGIN", "uh oh");
                        toClient.println(gson.toJson(failedLogin));
                        toClient.flush();
                    }else {
                        Message successfulLogin = new Message("LOGINOK", loginRequest);
                        toClient.println(gson.toJson(successfulLogin));
                        toClient.flush();
                    }
                    break;
                case "REGISTER": // check uniqueness of username
                    String username = message.getUser().getUsername();
                    System.out.println("register messsage received from: " + clientSocket.toString());
                    CustomerInfo newCustomer = database.findUserInfo(username);
                    if(newCustomer == null){
                        database.insertUserInfo(message.getUser());
                        toClient.println(gson.toJson(new Message("LOGINOK",message.getUser())));
                        toClient.flush();
                    }else {
                        toClient.println(gson.toJson(new Message("USER ALREADY EXISTS", "nice")));
                        toClient.flush();
                    }
                    break;
                case "BID": //bids need to be greater than the what the database's information has
                    Item dbItem = database.getItem(message.getItem().getName());
                    if(message.getBid() > dbItem.getCurrentBid()){
                        if(message.getBid() >= dbItem.getBuyItNowPrice()){
                            database.updateItem(message.getItem(), message.getBid(), message.getUsername());
                            database.moveItemToSold(message.getItem().getName());
                        }else {
                            database.updateItem(message.getItem(), message.getBid(), message.getUsername());
                        }
                    }
                    break;
                case "ADDITEM": // adds item to database
                    System.out.println("new item added, some info: ");
                    System.out.println(message.getItem().toString());
                    database.insertItem(message.getItem());
                    break;
                case "REMOVEITEM": // removes item from database
                    database.removeItem(message.getItem().getName());
                    break;
                case "QUIT": // closes the socket connection
                    System.out.println("removing connection to " + clientSocket.toString());
                    clientSocket.close();
                    break;
            }
            this.setChanged();

            //creating an active bid list and non active bid list to send to all clients, pulling from database
            itemsList = new ArrayList<Item>();
            database.items.find().forEach((Block<? super Document>) document -> {
                Item convertToItem = gson.fromJson(document.toJson(), Item.class);
                itemsList.add(convertToItem);
            });
            soldItems = new ArrayList<>();
            database.soldItems.find().forEach((Block<? super Document>) document -> {
                Item convertToItem = gson.fromJson(document.toJson(), Item.class);
                soldItems.add(convertToItem);
            });
            this.notifyObservers(gson.toJson(new Message("UPDATE", itemsList, soldItems))); //update screen of all clients
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}