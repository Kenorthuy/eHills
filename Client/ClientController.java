import com.jfoenix.controls.*;
import com.mongodb.client.MongoCollection;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.*;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController implements Initializable{
    Client myClient;

    Stage primaryStage;

    ClientController mainController;

    //main GUI
    Scene mainScene;
    @FXML
    private HBox applicationPane;
    @FXML
    private Label loginNotif;
    @FXML
    private Label welcomeMessage;
    @FXML
    private StackPane loginPane;
    @FXML
    private StackPane registerPane;
    @FXML
    private JFXButton clientLogin;
    @FXML
    private JFXButton removeItemButton;
    @FXML
    private JFXListView<Label> itemList;
    @FXML
    private JFXListView<Label> soldItemsList;
    @FXML
    private Label itemLabelPreview;
    @FXML
    private ImageView itemImagePreview;
    @FXML
    private JFXButton musicToggle;
    @FXML
    private JFXTextArea itemDescriptionPreview;
    @FXML
    private Label itemCurrentBidLabelPreview;
    @FXML
    private Label itemNamePreview;
    @FXML
    private Label itemBuyItNowLabelPreview;
    @FXML
    private JFXTextField inputBidAmount;
    @FXML
    private StackPane mainPane;

    //login popup
    @FXML
    private StackPane loginRegisterPane;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField email;

    @FXML
    private JFXTextField userNameFieldRegister;
    @FXML
    private JFXPasswordField passwordFieldRegister;
    @FXML
    private JFXTextField userNameFieldLogin;
    @FXML
    private JFXPasswordField passwordFieldLogin;

    //add item popup
    @FXML
    private StackPane addPane;
    @FXML
    private JFXTextField itemName;
    @FXML
    private JFXTextArea description;
    @FXML
    private JFXButton uploadButton;
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton quitButton;
    @FXML
    private JFXTextField BINPrice;
    @FXML
    private JFXTextField imageURLInput;
    @FXML
    private ImageView imagePreview;

    private File file = new File("/noImage.jpg");
    private Image questionMarkImage = new Image("/noImage.jpg");
    static CustomerInfo customerInfo;
    boolean loggedIn = false;
    boolean playing = false;
//    File song = new File("avatarsong.mp3");
    URL resource = getClass().getResource("avatarsonglong.mp3");
    Media menuMusic = new Media(resource.toString());
    MediaPlayer menuPlayer = new MediaPlayer(menuMusic);

    List<Item> items = new ArrayList<Item>();
    List<Item> soldItems = new ArrayList<>();
    List<Label> itemLabelList = new ArrayList<>();
    List<Label> soldItemsLabelList = new ArrayList<>();
    Map<String, Item> itemNametoItem = new HashMap<>();


    /**
     * login
     * @param event
     * takes user input to attempt a login into the database of user information
     */
    @FXML
    void login(ActionEvent event) {
        String username = userNameFieldLogin.getText();
        String password = passwordFieldLogin.getText();
        System.out.println(username);
        System.out.println(password);
        Message loginMessage = new Message("LOGIN", new CustomerInfo(username,password));
        myClient.sendToServer(loginMessage);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::showRestOfGUI, 2, TimeUnit.SECONDS);
    }

    /**
     * shows the rest of the GUI after a successful login/registration
     */
    protected void showRestOfGUI() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(loggedIn == false){
                    retryLogin();
                }else {
                    mainPane.setVisible(true);
                    addPane.setVisible(true);
//            loginNotif.setVisible(false);
                    loginRegisterPane.getChildren().removeAll(loginRegisterPane.getChildren());
                    applicationPane.getChildren().remove(loginRegisterPane);
//                    loginPane.setVisible(false);
//                    registerPane.setVisible(false);
//                    loginNotif.setStyle("-fx-border-color: #000000; -fx-text-fill: #000000; -fx-border-width: 3; -fx-border-width: 0");
                    welcomeMessage.setText("Welcome " + myClient.customerInfo.getFirstName() + "!");
                }
            }
        });
    }

    // requests user to retry their login
    void retryLogin() {
        userNameFieldLogin.clear();
        passwordFieldLogin.clear();
        userNameFieldLogin.setPromptText("Invalid Username/Password. Try again.");
    }

    /**
     * registerNewUser
     * @param event
     * registers a new user into the database, adds a new Mongo document if successful, but will return an error to the user
     * if the username is not unique
     */
    @FXML
    void registerNewUser(ActionEvent event) {
        String firstNameInput = firstName.getText();
        String emailInput = email.getText();
        String user = userNameFieldRegister.getText();
        String password = passwordFieldRegister.getText();
        System.out.println(firstNameInput);
        System.out.println(emailInput);
        System.out.println(user);
        System.out.println(password);
        CustomerInfo newCustomer = new CustomerInfo(user, password, firstNameInput, emailInput);
        myClient.customerInfo = newCustomer;
        Message registerMessage = new Message("REGISTER", newCustomer);
        myClient.sendToServer(registerMessage);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::showRestOfGUI, 2, TimeUnit.SECONDS);
    }

    /**
     * add
     * @param event
     * adds an item into the database
     */
    @FXML
    void add(ActionEvent event) {
        String itemNameInput = "";
        String descriptionInput = "";
        String buyItNowInputString = "";
        Double buyItNowPrice = null;
        String imageUrl;
        try {
            itemNameInput = itemName.getText();
            descriptionInput = description.getText();
            buyItNowInputString = BINPrice.getText();
            if(itemNameInput.equals("") | descriptionInput.equals("") | buyItNowInputString.equals("")) throw new Exception();
            imageUrl = imageURLInput.getText();
            if(!buyItNowInputString.equals("")){
                buyItNowPrice = Double.parseDouble(buyItNowInputString);
            }
            if(!isValidImageURL(imageUrl)){
                imageUrl = null;
            }
            Item newItem = new Item(itemNameInput,descriptionInput, buyItNowPrice, myClient.customerInfo.getUsername(),imageUrl);
            Message output = new Message("ADDITEM", newItem);
            clearAddItemFields();
            myClient.sendToServer(output);
            itemName.setPromptText("");
            description.setPromptText("");
            BINPrice.setPromptText("");
        }catch(NumberFormatException e){
            BINPrice.clear();
            BINPrice.setPromptText("Please fill out");
        }catch(Exception e){
            itemName.setPromptText("Please fill out");
            description.setPromptText("Please fill out");
            BINPrice.setPromptText("Please fill out");
        }
    }

    /**
     * clearAddItemFields
     * clears the add items section
     */
    private void clearAddItemFields() {
        itemName.clear();
        description.clear();
        imageURLInput.clear();
        BINPrice.clear();
        imagePreview.setImage(null);
    }

    /**
     * uploadPicture
     * @param event
     * takes a user inputted link and preview it to the user
     */
    @FXML
    void uploadPicture(ActionEvent event) throws IOException {
        String imageUrl = imageURLInput.getText();
        if(imageUrl.equals("")){
            imageUrl = null;
            imageURLInput.clear();
            imageURLInput.setPromptText("please input a link to a jpg ONLY");
            imagePreview.setImage(questionMarkImage);
            return;
        }
        String extension = imageUrl.substring(imageUrl.lastIndexOf("."));
        if(extension.equals(".jpg")){
            BufferedImage img = ImageIO.read(new URL(imageUrl));
//            ByteArrayOutputStream imgout = ImageIO.write();
            byte[] buffer = ((DataBufferByte)(img).getRaster().getDataBuffer()).getData();
            Image image1 = new Image(new ByteArrayInputStream(buffer));
            System.out.println(image1.toString());
            imagePreview.setImage(image1);
        } else {
            imageUrl = null;
            imageURLInput.clear();
            imageURLInput.setPromptText("please input a link to a jpg ONLY");
            imagePreview.setImage(questionMarkImage);
        }
    }

    /**
     * isValidImageURL
     * @param url
     * @return boolean
     * determines if an online link is a .jpg
     */
    boolean isValidImageURL(String url) {
        if(url.equals("")) return false;
        String extension = url.substring(url.lastIndexOf("."));
        return extension.equals(".jpg");
    }

    /**
     * removeItem
     * @param event
     * requests to remove a selected item from the MongoDB database
     */
    @FXML
    void removeItem(ActionEvent event) {
        try {
            Item itemToRemove = getCurrentSelectedItem();
            Message removeItem = new Message("REMOVEITEM", itemToRemove);
            myClient.sendToServer(removeItem);
        }catch(Exception e){
        }
    }

    /**
     * placeBid
     * @param event
     * places a bid from the user; communicates with host server to verify if bid works
     */
    @FXML
    void placeBid(ActionEvent event) {
        try {
            Item item = getCurrentSelectedItem();
            Double bidInput = Double.parseDouble(inputBidAmount.getText());
            if(bidInput <= item.getCurrentBid()) throw new Exception();
            Message placeBid = new Message("BID", item, bidInput, myClient.customerInfo.getUsername());
            myClient.sendToServer(placeBid);
            inputBidAmount.clear();
            inputBidAmount.setPromptText("Input your bid here");
        }catch (Exception e){
            inputBidAmount.clear();
            inputBidAmount.setPromptText("Bid too low");
        }
    }

    /**
     * getItemInfoList
     * @param items
     * @return list of labels for listview
     * converts a list of items into a list of labels
     */
    List<Label> getItemInfoList(List<Item> items){
        try {
            ArrayList<Label> list = new ArrayList<>();
            itemNametoItem = new HashMap<>();
            ImageView icon;
            for (Item i : items) {
                if(i.getImageURL() != null) {
                    icon = new ImageView(new Image(i.getImageURL()));
                    icon.setFitHeight(100);
                    icon.setFitWidth(100);
                } else {
                    icon = new ImageView(new Image("/checkmarkIcon.jpg"));
                    icon.setFitHeight(100);
                    icon.setFitWidth(100);
                }
                itemNametoItem.put(i.getName(), i);
                Label label = new Label(i.getName() + " from " + i.getOwnerUsername());
                label.setGraphic(icon);
                list.add(label);
            }
            return list;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * getSoldItemInfoList
     * @param items
     * @return List of labels for ListView
     * converts a list of items into a list of labels
     */
    List<Label> getSoldItemInfoList(List<Item> items){
        try {
            ArrayList<Label> list = new ArrayList<>();
            ImageView icon;
            for (Item i : items) {
                if(i.getImageURL() != null) {
                    icon = new ImageView(new Image(i.getImageURL()));
                    icon.setFitHeight(100);
                    icon.setFitWidth(100);
                } else {
                    icon = new ImageView(new Image("/checkmarkIcon.jpg"));
                    icon.setFitHeight(100);
                    icon.setFitWidth(100);
                }
                Label label = new Label(i.getName() + " sold to " + i.getTopBidderUsername());
                label.setGraphic(icon);
                list.add(label);
            }
            return list;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * updateItemsList
     * updates the ListView of both active and inactive bids
     */
    void updateItemsList() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            //TODO: 1. create new list
//                    2. get every document from items collection in Mongo
                soldItemsList.getItems().clear();
                itemList.getItems().clear();
                itemLabelList = getItemInfoList(items);
                soldItemsLabelList = getSoldItemInfoList(soldItems);
                for(Label l: itemLabelList){
                    itemList.getItems().add(l);
                }
                for(Label l: soldItemsLabelList){
                    soldItemsList.getItems().add(l);
                }

                clearPreview();

            }
        });
    }

    /**
     * clearPreview
     * clears the preview section of the GUI
     */
    private void clearPreview() {
        itemNamePreview.setText("Item Name");
        itemImagePreview.setImage(null);
        itemDescriptionPreview.clear();
        itemCurrentBidLabelPreview.setText("Current Bid:");
        itemBuyItNowLabelPreview.setText("Buy It Now:");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setVisible(false);
        addPane.setVisible(false);
        System.setProperty("http.agent", "Chrome");
        menuPlayer.setVolume(.09);
        menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        itemList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Item selectedItem = getCurrentSelectedItem();
                try {
                    itemNamePreview.setText(selectedItem.getName());
                    if(selectedItem.getImageURL() !=null) {
                        itemImagePreview.setImage(new Image(selectedItem.getImageURL()));
                    }else {
                        itemImagePreview.setImage(new Image("/checkmarkIcon.jpg"));
                    }
                    itemDescriptionPreview.setText(selectedItem.getDescription());
                    itemCurrentBidLabelPreview.setText("Current Bid: $" + selectedItem.getCurrentBid() + " by " + selectedItem.getTopBidderUsername());
                    itemBuyItNowLabelPreview.setText("Buy it Now: $" + selectedItem.getBuyItNowPrice());
                }catch(Exception e){
                }
//                Item itemSelected = getUniqueItem(items, username);
            }
        });

    }

    /**
     * toggleMusic
     * @param event
     * toggles music playback
     */
    @FXML
    void toggleMusic(ActionEvent event) {
        if(!playing) {
            menuPlayer.play();
            playing = true;
        }
        else {
            menuPlayer.pause();
            playing = false;
        }
    }

    /**
     * getCurrentSelectedItem
     * @return the item that the user selected from the ListView
     */
    public Item getCurrentSelectedItem() {
        try {
            String selected = itemList.getSelectionModel().getSelectedItem().toString();
            System.out.println("clicked on " + selected);
            String prefix = "label]'";
            String suffix = " from";
            String itemName = selected.substring(selected.indexOf(prefix) + 7, selected.indexOf(suffix));
            Item selectedItem = itemNametoItem.get(itemName);
            return selectedItem;
        }catch(Exception e) {
            return null;
        }
    }

    /**
     * quit
     * @param event
     */
    @FXML
    void quit(ActionEvent event) {
        myClient.quit();
    }

    /**
     * getItems
     * @return the list of items found in the controller
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * setItems
     * @param items sets the reference variable of the items list
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * getSoldItems
     * @return return the list of sold items
     */
    public List<Item> getSoldItems() {
        return soldItems;
    }

    /**
     * setSoldItems
     * @param soldItems set the list of sold items
     */
    public void setSoldItems(List<Item> soldItems) {
        this.soldItems = soldItems;
    }
}
