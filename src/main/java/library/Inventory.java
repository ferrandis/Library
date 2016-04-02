package library;

import java.util.List;
import java.util.Set;

public interface Inventory {
    Set<String> currentInventory();

    Item remove(String title);

    void add(Item item);

    boolean available(String item);

    List<Item> currentStock();
}
