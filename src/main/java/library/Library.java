package library;

import exceptions.ItemNotFoundException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Library {
    private final Inventory inventory;
    private final Loans loans;
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Clock clock;

    public Library(Clock clock, Inventory inventory, Loans loans) {
        this.clock = clock;
        this.inventory = inventory;
        this.loans = loans;
    }

    public Receipt borrowItem(String title, User user) throws ItemNotFoundException {
        rwl.readLock().lock();
        try {

            if (inventory.available(title)) {
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                try {
                    Item borrowedItem = inventory.remove(title);
                    LocalDate returnDate = LocalDate.now(clock).plusWeeks(1L);

                    loans.add(borrowedItem, returnDate, user);
                    rwl.readLock().lock();
                    return new Receipt(returnDate, borrowedItem);

                } finally {
                    rwl.writeLock().unlock();
                }

            }

            throw ItemNotFoundException.itemNotFound(title);
        } finally {
            rwl.readLock().unlock();
        }
    }

    public void returnItem(Item item) {
        rwl.writeLock().lock();
        try {
            inventory.add(item);
            loans.remove(item);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public Set<String> currentInventory() {
        rwl.readLock().lock();
        try {
            return inventory.currentInventory();
        } finally {
            rwl.readLock().unlock();
        }
    }

    public List<Item> overDueItems() {
        rwl.readLock().lock();
        try {
            return loans.overDueItems(LocalDate.now(clock));
        } finally {
            rwl.readLock().unlock();
        }
    }

    public List<Item> borrowedItemsBy(User user) {
        rwl.readLock().lock();
        try {
            return loans.borrowedBy(user);
        } finally {
            rwl.readLock().unlock();
        }
    }

    public List<Item> currentStock() {
        rwl.readLock().lock();
        try {
            return inventory.currentStock();
        } finally {
            rwl.readLock().unlock();
        }
    }
}
