package contact;

/**
 * Created by Loky on 03/06/2018.
 */
public class Contact {

//    client_name, street_address, postal_code, location, region, phone_numbers, email_address

    private String clientName = "";
    private String address = "";
    private String location = "";
    private String phone_numbers = "";
    private String email_address = "";
    private String web_address = "";

    public Contact(String clientName, String address, String location, String phone_numbers, String web_address) {
        this.clientName = clientName;
        this.address = address;
        this.location = location;
        this.phone_numbers = phone_numbers;
        this.web_address = web_address;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(String phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getWeb_address() {
        return web_address;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "clientName='" + clientName + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", phone_numbers='" + phone_numbers + '\'' +
                ", web_address='" + web_address + '\'' +
                '}';
    }

    public String getLocation() {
        return location;
    }
}
