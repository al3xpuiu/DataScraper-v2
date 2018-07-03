import contact.Contact;

import java.sql.*;

/**
 * Created by Loky on 21/04/2018.
 */
public class ToDataBaseWriter {

    public static void write(Contact contact) {

        String url = "jdbc:mysql://localhost:3306/data_base?autoReconnect=true&useSSL=false";
        String user = "user";
        String password = "password";


        String sql = "INSERT INTO database (client_name, address, location, phone_numbers, web_address)" +
                " VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection( url, user, password );
             PreparedStatement statement = connection.prepareStatement( sql )) {

            statement.setString( 1, contact.getClientName( ) );
            statement.setString( 2, contact.getAddress( ) );
            statement.setString( 3, contact.getLocation() );
            statement.setString( 4, contact.getPhone_numbers() );
            statement.setString( 5, contact.getWeb_address() );


            statement.executeUpdate();

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            System.out.println( contact );
        }

    }
}
