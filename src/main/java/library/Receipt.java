package library;

import java.time.LocalDate;

public class Receipt {
    public final LocalDate returnDate;
    public final Item item;

    public Receipt(LocalDate returnDate, Item item) {
        this.returnDate = returnDate;
        this.item = item;
    }

    public static Receipt receipt(LocalDate returnDate, Item item) {
        return new Receipt(returnDate, item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        if (returnDate != null ? !returnDate.equals(receipt.returnDate) : receipt.returnDate != null) return false;
        return !(item != null ? !item.equals(receipt.item) : receipt.item != null);

    }

    @Override
    public int hashCode() {
        int result = returnDate != null ? returnDate.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
