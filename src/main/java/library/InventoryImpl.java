package library;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class InventoryImpl implements Inventory {
    private Map<String, List<Item>> currentInventory;

    public InventoryImpl(Map<String, List<Item>> inventory) {
        this.currentInventory = inventory;
    }

    @Override
    public Set<String> currentInventory() {
        return currentInventory.keySet();
    }

    @Override
    public Item remove(String title) {
        Item removed = currentInventory.get(title).remove(0);
        if (currentInventory.get(title).isEmpty()) {
            currentInventory.remove(title);
        }
        return removed;
    }

    @Override
    public void add(Item item) {
        if (currentInventory.containsKey(item.title)) {
            currentInventory.get(item.title).add(item);
        } else {
            currentInventory.put(item.title, newArrayList(item));
        }
    }

    @Override
    public boolean available(String title) {
        return currentInventory.containsKey(title) && !currentInventory.get(title).isEmpty();
    }

    @Override
    public List<Item> currentStock() {
        return currentInventory.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
