import java.io.Serializable;

public class Item implements Serializable {
    private String name;
    private String description;
    private Double buyItNowPrice;
    private Double currentBid;
    private String topBidderUsername;
    private String ownerUsername;
    private String imageURL;
    private Server server;

    public Item(String name, String description, Double buyItNowPrice, Integer bidLength, String ownerUsername, String imageURL, Server server) {
        this.server = server;
        this.name = name;
        this.description = description;
        this.buyItNowPrice = buyItNowPrice;
        this.currentBid = 0.00;
        this.ownerUsername = ownerUsername;
        this.topBidderUsername = "";
        this.imageURL = imageURL;
    }

    public Item(String name, String description, Double buyItNowPrice, Integer bidLength, String ownerUsername, String imageURL) {
        this.server = null;
        this.name = name;
        this.description = description;
        this.buyItNowPrice = buyItNowPrice;
        this.currentBid = 0.00;
        this.topBidderUsername = "";
        this.ownerUsername = ownerUsername;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBuyItNowPrice() {
        return buyItNowPrice;
    }

    public void setBuyItNowPrice(Double price) {
        this.buyItNowPrice = price;
    }

    public Double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(Double currentBid) {
        this.currentBid = currentBid;
    }

    public String getTopBidderUsername() {
        return topBidderUsername;
    }

    public void setTopBidderUsername(String topBidderUsername) {
        this.topBidderUsername = topBidderUsername;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", buyItNowPrice=" + buyItNowPrice +
                ", currentBid=" + currentBid +
                ", topBidderUsername='" + topBidderUsername + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                '}';
    }

}
