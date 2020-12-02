import com.google.gson.Gson;
import java.io.Serializable;

public class CustomerInfo implements Serializable {


    private static Integer customerIdCount = 0;
    private Integer customerId;
    private String username;
    private String password;
    private String firstName;
    private String email;
    //    private Cart cart;
    private Gson gson;

    public CustomerInfo(String username, String password, String firstName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.email = email;
        customerId = getNextCustomerId();
//        this.cart = new Cart();
    }

    public CustomerInfo(String username, String password) {
        this.username = username;
        this.password = password;
//        this.cart = new Cart();
    }

    private static synchronized Integer getNextCustomerId() {
        return customerIdCount++;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    //    private class Cart {
//        private List<Item> items;
//        private float totalPrice;
//
//        Cart() {
//            items = new ArrayList<Item>();
//            totalPrice = 0;
//        }
//    }

}
