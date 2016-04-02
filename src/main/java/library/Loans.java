package library;

import java.time.LocalDate;
import java.util.List;

public interface Loans {
    void add(Item item, LocalDate dueDate, User user);

    List<Item> overDueItems(LocalDate today);

    List<Item> borrowedBy(User user);

    boolean remove(Item item);
}
