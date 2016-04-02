package library;

import java.time.LocalDate;

public class Loan {
    public final Item item;
    public final LocalDate dueDate;
    public final User user;

    public Loan(Item item, LocalDate dueDate, User user) {

        this.item = item;
        this.dueDate = dueDate;
        this.user = user;
    }

    public static Loan loan(Item item, LocalDate dueDate, User user) {
        return new Loan(item, dueDate, user);
    }
}
