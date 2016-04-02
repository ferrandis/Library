package library.unit;

import exceptions.ItemNotFoundException;
import library.*;
import library.util.MoveableClock;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static library.Receipt.receipt;
import static library.User.user;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LibraryTest {
    private final Mockery context = new Mockery();
    private final Inventory inventory = context.mock(Inventory.class);
    private final Loans loans = context.mock(Loans.class);

    @Test
    public void canBorrowAnItemIfAvailable() throws ItemNotFoundException {
        MoveableClock clock = new MoveableClock();
        final Library library = new Library(clock, inventory, loans);

        Item item = new Item(1, 7, ItemType.DVD, "Pi");
        User user = user(1, "Oscar");


        context.checking(new Expectations() {
            {
                one(inventory).available(item.title);
                will(returnValue(true));
                one(inventory).remove(item.title);
                will(returnValue(item));
                one(loans).add(item, LocalDate.now(clock).plusWeeks(1L), user);
            }
        });
        assertThat(library.borrowItem(item.title, user), equalTo(receipt(LocalDate.now(clock).plusWeeks(1L), item)));

    }

    @Test
    public void canNotBorrowAnItemIfUnavailable() throws ItemNotFoundException {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);


        Item item = new Item(1, 7, ItemType.DVD, "Pi");
        User user = user(1, "Oscar");


        context.checking(new Expectations() {
            {
                one(inventory).available(item.title);
                will(returnValue(false));

            }
        });
        try {
            library.borrowItem(item.title, user);
            fail();
        } catch (ItemNotFoundException e) {
            assertThat(e.getMessage(), equalTo("Could not find the " + item.title + ", you want to borrow"));
        }


    }

    @Test
    public void canReturnItem() {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);

        Item item = new Item(1, 7, ItemType.DVD, "Pi");
        context.checking(new Expectations() {
            {
                one(inventory).add(item);
                one(loans).remove(item);
            }
        });
        library.returnItem(item);

    }

    @Test
    public void canDeterminCurrentInventory() {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);

        Set<String> expectedLoanableItems = newHashSet("Pi", "The Art Of Computer Programming Volumes 1-6");
        context.checking(new Expectations() {
            {
                one(inventory).currentInventory();
                will(returnValue(expectedLoanableItems));
            }
        });

        assertThat(library.currentInventory(), equalTo(expectedLoanableItems));
    }

    @Test
    public void canDetreminOverDueItems() {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);

        List<Item> expectedOverDueItems = newArrayList(new Item(1, 1, ItemType.Book, "Pi"), new Item(2, 3, ItemType.DVD, "Pipe"));

        clock.moveForward(8, ChronoUnit.DAYS);
        context.checking(new Expectations() {
            {
                one(loans).overDueItems(LocalDate.now(clock));
                will(returnValue(expectedOverDueItems));
            }
        });

        assertThat(library.overDueItems(), equalTo(expectedOverDueItems));
    }

    @Test
    public void canDetreminBorrowedItemsPerUser() {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);

        User user = User.user(1, "Oscar");

        List<Item> expectedBorrowedItems = newArrayList(new Item(1, 1, ItemType.Book, "Pi"), new Item(2, 3, ItemType.DVD, "Pipe"));

        context.checking(new Expectations() {
            {
                one(loans).borrowedBy(user);
                will(returnValue(expectedBorrowedItems));
            }
        });

        assertThat(library.borrowedItemsBy(user), equalTo(expectedBorrowedItems));
    }

    @Test
    public void canGetAllItemsOfCurrentStock() {
        MoveableClock clock = new MoveableClock();
        Library library = new Library(clock, inventory, loans);

        List<Item> expectedCurrentStock = newArrayList(new Item(1, 1, ItemType.Book, "Pi"), new Item(2, 3, ItemType.DVD, "Pipe"));

        context.checking(new Expectations() {
            {
                one(inventory).currentStock();
                will(returnValue(expectedCurrentStock));
            }
        });
        assertThat(library.currentStock(), equalTo(expectedCurrentStock));
    }


}
