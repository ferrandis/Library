package library;

import exceptions.ItemNotFoundException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Library implements Libraries{
    private final Inventory inventory;
    private final Loans loans;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Clock clock;

    public Library(Clock clock, Inventory inventory, Loans loans) {
        this.clock = clock;
        this.inventory = inventory;
        this.loans = loans;
    }

    @Override
    public Receipt borrowItem(String title, User user) throws ItemNotFoundException {
            if (inventory.available(title)) {
                synchronized(this) {
                    Item borrowedItem = inventory.remove(title);
                    LocalDate returnDate = LocalDate.now(clock).plusWeeks(1L);

                    loans.add(borrowedItem, returnDate, user);

                    return new Receipt(returnDate, borrowedItem);
                }

            }

            throw ItemNotFoundException.itemNotFound(title);

    }

    @Override
    public synchronized void returnItem(Item item) {

            inventory.add(item);
            loans.remove(item);

    }

    @Override
    public Set<String> currentInventory() {

            return inventory.currentInventory();

    }

    @Override
    public List<Item> overDueItems() {

            return loans.overDueItems(LocalDate.now(clock));

    }

    @Override
    public List<Item> borrowedItemsBy(User user) {

            return loans.borrowedBy(user);

    }

    @Override
    public List<Item> currentStock() {

            return inventory.currentStock();

    }
}
