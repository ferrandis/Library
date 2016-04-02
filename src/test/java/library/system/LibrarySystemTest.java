package library.system;

import library.*;
import library.util.MoveableClock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.nCopies;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LibrarySystemTest {
    @Test
    public void canHandleLargeInventory() throws InterruptedException {

        Map<String, List<Item>> initialInventory = newHashMap();
        initialInventory.put("book title", newArrayList());
        initialInventory.put("dvd title", newArrayList());
        initialInventory.put("movie title", newArrayList());
        initialInventory.put("vhs title", newArrayList());
        initialInventory.put("book 2", newArrayList());

        int initialStockSize = 1000000;
        for (int i = 1; i <= initialStockSize; i += 5) {
            initialInventory.get("book title").add(new Item(i, i + 5, ItemType.Book, "book title"));
            initialInventory.get("dvd title").add(new Item(i, i + 6, ItemType.DVD, "dvd title"));
            initialInventory.get("movie title").add(new Item(i, i + 7, ItemType.Movie, "movie title"));
            initialInventory.get("vhs title").add(new Item(i, i + 8, ItemType.VHS, "vhs title"));
            initialInventory.get("book 2").add(new Item(i, i + 9, ItemType.Book, "book 2"));

        }

        InventoryImpl inventory = new InventoryImpl(initialInventory);

        List<Item> initialStock = inventory.currentStock();
        System.out.println("Finished populating initial inventory with " + initialStock.size() + " items.");

        MoveableClock clock = new MoveableClock();

        Library library = new Library(clock, inventory, new LoansImpl());

        System.out.println("Library has inventory of items: " + library.currentInventory());

        int numberOfAttemptToBorrowBook1 = 700;
        User user1 = User.user(10, "Oscar");

        System.out.println(user1.name + " is trying to borrow " + numberOfAttemptToBorrowBook1 + " books.");
        List<Future<Receipt>> attemptsToBorrowBook1 = newFixedThreadPool(10).invokeAll(
                nCopies(numberOfAttemptToBorrowBook1, () -> {
                    return library.borrowItem("book title", user1);
                })
        );

        int numberOfAttemptToBorrowBook2 = 300;
        User user2 = User.user(20, "Erin");
        System.out.println(user2.name + " is trying to borrow " + numberOfAttemptToBorrowBook2 + " books.");

        List<Future<Receipt>> attemptsToBorrowBook2 = newFixedThreadPool(10).invokeAll(
                nCopies(numberOfAttemptToBorrowBook2, () -> {
                    return library.borrowItem("book 2", user2);
                })
        );

        System.out.println(user1.name + " has borrowed " + library.borrowedItemsBy(user1).size() + " books.");
        System.out.println(user2.name + " has borrowed " + library.borrowedItemsBy(user2).size() + " books.");


        int borrowedBookSize = attemptsToBorrowBook1.size() + attemptsToBorrowBook2.size();
        assertThat(borrowedBookSize, equalTo(numberOfAttemptToBorrowBook1 + numberOfAttemptToBorrowBook2));

        int stockSizeAfterBorrowing = initialStockSize - borrowedBookSize;
        assertThat(library.currentStock().size(), equalTo(stockSizeAfterBorrowing));

        System.out.println("Borrowed " + borrowedBookSize + " items from library. Current stock size is " + library.currentStock().size());

        List<Receipt> actualBook1ItemsBorrowed = new ArrayList<>();
        for (Future<Receipt> receiptFuture : attemptsToBorrowBook1) {
            try {
                actualBook1ItemsBorrowed.add(receiptFuture.get());
            } catch (ExecutionException ignore) {
            }
        }

        List<Callable<Void>> book1ToReturn = new ArrayList<>();
        for (Receipt receiptFromBorrowedItem : actualBook1ItemsBorrowed) {
            book1ToReturn.add(() -> {
                library.returnItem(receiptFromBorrowedItem.item);
                return null;
            });
        }

        for (Future<Void> itemBeingReturned : newFixedThreadPool(10).invokeAll(book1ToReturn)) {
            try {
                itemBeingReturned.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        assertThat(library.currentStock().size(), equalTo(stockSizeAfterBorrowing + book1ToReturn.size()));

        System.out.println("Returned " + actualBook1ItemsBorrowed.size() + " items to library. Now current stock size is " + library.currentStock().size());

        //move time forward 10 days
        System.out.println("After 10 days...");
        clock.moveForward(10, DAYS);

        List<Receipt> actualBook2ItemsBorrowed = new ArrayList<>();
        for (Future<Receipt> receiptFuture : attemptsToBorrowBook2) {
            try {
                actualBook2ItemsBorrowed.add(receiptFuture.get());
            } catch (ExecutionException ignore) {
            }
        }

        assertThat(library.overDueItems().size(), equalTo(actualBook2ItemsBorrowed.size()));
        assertTrue(library.overDueItems().containsAll(actualBook2ItemsBorrowed.stream().map(b -> b.item).collect(Collectors.toList())));

        System.out.println("We have overdue items of " + library.overDueItems().size());
        System.out.println("Try to return them all.");

        List<Callable<Void>> book2ToReturn = new ArrayList<>();

        for (Receipt receiptFromBorrowedItem : actualBook2ItemsBorrowed) {
            book2ToReturn.add(() -> {
                library.returnItem(receiptFromBorrowedItem.item);
                return null;
            });
        }
        for (Future<Void> itemBeingReturned : newFixedThreadPool(10).invokeAll(book2ToReturn)) {
            try {
                itemBeingReturned.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Now we have all books back. Current stock is " + library.currentStock().size());
        assertThat(library.currentStock().size(), equalTo(initialStock.size()));


    }
}
