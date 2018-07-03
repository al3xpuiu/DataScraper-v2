package contact;

import java.util.Queue;

/**
 * Created by Loky on 21/06/2018.
 */
public class ContactFactory {

    public static Contact getContact(Queue<String> contactData) {

        String clientName = assign( contactData );

        String address = "";
        String locality = "";
        if (!contactData.peek().contains( "www." ) && !contactData.peek().contains( "tel" )) {
            String fullAddress = contactData.poll();
            try {
                String[] temp = fullAddress.split( "," );
                address = temp[0];
                locality = temp[1];

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println( "Address most probably is not complete. " + e.getMessage() );
                address = fullAddress;
                locality = "";
            }
        }

        String phone_numbers = "";
        String web_address = "";

        while (!contactData.isEmpty()) {
            String data = contactData.poll();

            if (data.contains( "tel" )) {
                if (!phone_numbers.isEmpty()) {
                    phone_numbers = phone_numbers.concat( " / " + data );
                } else {
                    phone_numbers = phone_numbers.concat( data );
                }
            }

            if (data.contains( "www." )) {
                web_address = web_address.concat( data );
            }

        }
        return new Contact( clientName, address, locality ,phone_numbers, web_address );
    }

    private static String assign(Queue<String> contactData) {
        if (!contactData.isEmpty())
                return contactData.poll();
        return "";
    }
}
