import contact.Contact;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Loky on 20/04/2018.
 */
public class ContactsQueue {

    private Queue<Contact> contactsQueue;
    private static volatile ContactsQueue instance;

    private ContactsQueue() {
        this.contactsQueue = new ConcurrentLinkedQueue<>();
    }

    public static ContactsQueue getInstance() {
        if (instance == null) {
            synchronized (ContactsQueue.class) {
                if (instance == null) instance = new ContactsQueue();
        }
        }

        return instance;
    }

    public Queue<Contact> getContactsQueue() {
        return contactsQueue;
    }
}
