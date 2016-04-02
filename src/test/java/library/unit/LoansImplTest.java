package library.unit;

import library.Item;
import library.ItemType;
import library.LoansImpl;
import library.User;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.LocalDate.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoansImplTest {

    @Test
    public void canListOverDueItems() {
        LoansImpl loans = new LoansImpl();
        Item item1 = new Item(1, 1, ItemType.Book, "Pi");
        loans.add(item1, of(2016, 3, 16), User.user(1, "Oscar"));

        Item item2 = new Item(2, 1, ItemType.Book, "Pi");
        loans.add(item2, of(2016, 3, 10), User.user(1, "Oscar"));

        Item item3 = new Item(3, 2, ItemType.DVD, "someDvd");
        loans.add(item3, of(2016, 3, 11), User.user(1, "Oscar"));

        assertTrue(loans.overDueItems(of(2016, 3, 11)).containsAll(newArrayList(item2)));
    }

    @Test
    public void canListBorrowedItemByUser() {
        LoansImpl loans = new LoansImpl();
        User user1 = User.user(1, "Oscar");
        User user2 = User.user(1, "Erin");

        Item item1 = new Item(1, 1, ItemType.Book, "Pi");
        loans.add(item1, of(2016, 3, 16), user1);

        Item item2 = new Item(2, 1, ItemType.Book, "Pi");
        loans.add(item2, of(2016, 3, 10), user2);

        Item item3 = new Item(3, 2, ItemType.DVD, "someDvd");
        loans.add(item3, of(2016, 3, 11), user1);

        assertTrue(loans.borrowedBy(user1).containsAll(newArrayList(item1, item3)));
    }

    @Test
    public void canRemoveFromLoans() {

        LoansImpl loans = new LoansImpl();
        User user1 = User.user(1, "Oscar");
        User user2 = User.user(1, "Erin");

        Item item1 = new Item(1, 1, ItemType.Book, "Pi");
        loans.add(item1, of(2016, 3, 16), user1);

        Item item2 = new Item(2, 1, ItemType.Book, "Pi");
        loans.add(item2, of(2016, 3, 10), user2);

        Item item3 = new Item(3, 2, ItemType.DVD, "someDvd");
        loans.add(item3, of(2016, 3, 11), user1);

        assertThat(loans.loanItems().size(), equalTo(3));
        assertTrue(loans.loanItems().containsAll(newArrayList(item1, item2, item3)));
        loans.remove(item1);
        assertThat(loans.loanItems().size(), equalTo(2));
        assertTrue(loans.loanItems().containsAll(newArrayList(item2, item3)));

    }
}