package library.unit;

import library.InventoryImpl;
import library.Item;
import library.ItemType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class InventoryImplTest {

    @Test
    public void canDetermineInventory() {

        Item item1 = new Item(1, 7, ItemType.DVD, "Pi");
        Item item2 = new Item(2, 1, ItemType.Book, "The Art Of Computer Programming Volumes 1-6");
        Item item3 = new Item(3, 7, ItemType.DVD, "Pi");
        Map<String, List<Item>> items = newHashMap();
        items.put("Pi", newArrayList(item1, item3));
        items.put("The Art Of Computer Programming Volumes 1-6", newArrayList(item2));
        InventoryImpl inventory = new InventoryImpl(items);

        assertTrue(inventory.currentInventory().containsAll(newHashSet("Pi", "The Art Of Computer Programming Volumes 1-6")));
    }

    @Test
    public void canBorrowAnItem() {
        Item item1 = new Item(1, 7, ItemType.DVD, "Pi");
        Item item2 = new Item(2, 1, ItemType.Book, "The Art Of Computer Programming Volumes 1-6");
        Item item3 = new Item(3, 7, ItemType.DVD, "Pi");
        Map<String, List<Item>> items = newHashMap();
        items.put("Pi", newArrayList(item1, item3));
        items.put("The Art Of Computer Programming Volumes 1-6", newArrayList(item2));
        InventoryImpl inventory = new InventoryImpl(items);

        inventory.remove("Pi");

        assertThat(inventory.currentInventory().size(), equalTo(2));

        assertTrue(inventory.currentInventory().containsAll(newHashSet("Pi", "The Art Of Computer Programming Volumes 1-6")));

    }


    @Test
    public void canReturnAnItem() {
        Item item1 = new Item(2, 1, ItemType.Book, "The Art Of Computer Programming Volumes 1-6");
        Map<String, List<Item>> items = newHashMap();
        items.put("The Art Of Computer Programming Volumes 1-6", newArrayList(item1));
        InventoryImpl inventory = new InventoryImpl(items);

        Item returningItem = new Item(3, 7, ItemType.DVD, "Pi");
        assertTrue(inventory.currentInventory().containsAll(newHashSet("The Art Of Computer Programming Volumes 1-6")));

        inventory.add(returningItem);

        assertTrue(inventory.currentInventory().containsAll(newHashSet("Pi", "The Art Of Computer Programming Volumes 1-6")));

    }

    @Test
    public void canReturnCurrentStock() {
        Item item1 = new Item(2, 1, ItemType.Book, "The Art Of Computer Programming Volumes 1-6");
        Map<String, List<Item>> items = newHashMap();
        items.put("The Art Of Computer Programming Volumes 1-6", newArrayList(item1));
        InventoryImpl inventory = new InventoryImpl(items);

        assertTrue(inventory.currentStock().containsAll(newArrayList(item1)));
    }

}
