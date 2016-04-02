package exceptions;

public class ItemNotFoundException extends Exception {

    public ItemNotFoundException(String message) {
        super(message);
    }

    public static ItemNotFoundException itemNotFound(String title) {
        return new ItemNotFoundException("Could not find the " + title + ", you want to borrow");
    }
}
