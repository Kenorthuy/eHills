import java.io.Serializable;

public class CustomerInfo implements Serializable {

    private static Integer customerIdCount = 0;
    private Integer customerId;
    private String username;
    private String password;
    private String firstName;
    private String email;
//    private Cart cart;
//    private Gson gson;
    public CustomerInfo() {
        username = null;
        password = null;
        firstName = null;
        email = null;
    }

    public CustomerInfo(String username, String password, String firstName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.email = email;
        customerId = CustomerInfo.getNextCustomerId();
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

    @Override
    public String toString() {
        return "CustomerInfo{" +
                "customerId=" + customerId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }


}
