import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.Serializable;
import java.util.List;


public class Message implements Serializable {
    private static final long serialVersionUID = 1234L;
    private String type;
    private String message;
    private String username;
    private String password;
    private int number;
    private Item item;
    private CustomerInfo user;
    MongoCollection<Document> documentList;
    List<Item> itemList;
    List<Item> soldItems;
    Double bid;
    public Message(String type, String message, String username, String password, int number) {
        this.type = type;
        this.message = message;
        this.username = username;
        this.password = password;
        this.number = number;
    }
    public Message(String type, Item item) {
        this.type = type;
        this.item = item;
    }

    public Message(String itemType, Item item, Double bid) {
        this.type = itemType;
        this.item = item;
        this.bid = bid;
    }

    public Message(String type, MongoCollection<Document> documentList) {
        this.type = type;
        this.documentList = documentList;
    }
    public Message(String type, List<Item> itemList, List<Item> soldItems) {
        this.type = type;
        this.itemList = itemList;
        this.soldItems = soldItems;
    }

    public Message(String type, String message) {
        this.type = type;
        this.message = message;
        this.username = null;
        this.password = null;
        this.number = 0;
    }

    public Message(String userType, CustomerInfo user) {
        this.type = userType;
        this.user = user;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public Item getItem() {
        return item;
    }
    public CustomerInfo getUser() {
        return user;
    }
    public List<Item> getItemList() {
        return itemList;
    }
    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public List<Item> getSoldItems() {
        return soldItems;
    }
}
