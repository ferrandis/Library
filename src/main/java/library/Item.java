package library;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static library.ItemType.valueOf;

public class Item {
    private final int id;
    private final int bookId;
    private final ItemType itemType;
    public final String title;

    public Item(int id, int bookId, ItemType itemType, String title) {
        this.id = id;
        this.bookId = bookId;
        this.itemType = itemType;
        this.title = title;
    }

    @Override
    public String toString() {
        return "{" + id + "," + bookId + "," + itemType + "," + title + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != item.id) return false;
        if (bookId != item.bookId) return false;
        if (itemType != item.itemType) return false;
        return !(title != null ? !title.equals(item.title) : item.title != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + bookId;
        result = 31 * result + (itemType != null ? itemType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    public static Item fromCsvLine(String line) {
        List<String> part = Arrays.asList(line.split(","));

        if (!(part.size() == 4)) {
            throw new RuntimeException("Can't create item from " + line);
        }
        return new Item(parseInt(part.get(0)), parseInt(part.get(1)), valueOf(part.get(2)), part.get(3));
    }
}
