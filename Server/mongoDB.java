import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class mongoDB {
    public MongoClient mongoClient;
    public MongoDatabase database;
    public MongoCollection<Document> userInfo;
    public MongoCollection<Document> items;
    public MongoCollection<Document> soldItems;
    public MongoCollection<CustomerInfo> userinfoPOJO;
    public Gson gson;
    public DBObject obj;

    public mongoDB() {
        initializeDatabase();
    }

    public void initializeDatabase() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://Kenorthuy:KVLN4lL8*$@422c-final-project.qoi8v.mongodb.net/422c?retryWrites=true&w=majority");
        gson = new GsonBuilder().setPrettyPrinting().create();
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("422c");
        userInfo = database.getCollection("userInfoTest");
        items = database.getCollection("itemsTest");
        soldItems = database.getCollection("soldItemsTest");
    }

    public void insertItem(Item item) {
        Document doc = Document.parse(gson.toJson(item));
        items.insertOne(doc);
    }

    public Item getItem(String name) {
        Document query = items.find(eq("name",name)).first();
        Item foundItem = gson.fromJson(query.toJson(),Item.class);
        return foundItem;
    }

    public void updateItem(Item item, Double bid, String name){
        items.updateOne(eq("name",item.getName()), combine(set("currentBid",bid), set("topBidderUsername", name)));
    }
    public void updateItem(Item item, Double bid){
        items.updateOne(eq("name",item.getName()), set("currentBid",bid));
    }

    public void removeItem(String name) {
        items.deleteOne(Filters.eq("name", name));
    }

    public void moveItemToSold(String name){
        Document query = items.find(eq("name",name)).first();
        Item foundItem = gson.fromJson(query.toJson(),Item.class);
        Document item = Document.parse(gson.toJson(foundItem));
        soldItems.insertOne(item);
        items.deleteOne(query);
    }

    public void insertUserInfo(CustomerInfo user) {
        Document doc = Document.parse(gson.toJson(user));
        userInfo.insertOne(doc);
    }

    public CustomerInfo getUserInfo(String username) {
        Document retrieveCustomerDoc = userInfo.find(eq("username",username)).first();
        CustomerInfo foundCustomer = gson.fromJson(retrieveCustomerDoc.toJson(),CustomerInfo.class);
        return foundCustomer;
    }

    public CustomerInfo findUserInfo(String username) {
        Document retrieveCustomerDoc = userInfo.find(eq("username",username)).first();
        if(retrieveCustomerDoc != null){
            return gson.fromJson(retrieveCustomerDoc.toJson(),CustomerInfo.class);
        }else {
            return null;
        }
    }

    public CustomerInfo findUserInfo(CustomerInfo user) {
        Document retrieveCustomerDoc = userInfo.find(new Document("username",user.getUsername()).append("password",user.getPassword())).first();
        if(retrieveCustomerDoc != null){
            return gson.fromJson(retrieveCustomerDoc.toJson(),CustomerInfo.class);
        }else {
            return null;
        }
    }


    private void printDatabases() {
        List<Document> strings = mongoClient.listDatabases().into(new ArrayList<>());
        strings.forEach(document -> System.out.println(document.toJson()));
    }

}
