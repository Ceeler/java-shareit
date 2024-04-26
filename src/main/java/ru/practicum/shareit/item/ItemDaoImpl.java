package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository("ItemMemoryDao")
@Deprecated
public class ItemDaoImpl implements ItemDao {

    private final Map<Integer, Item> items = new HashMap<>();

    private Integer itemIdGenerator = 0;

    @Override
    public Item save(Item item) {
        final int id = ++itemIdGenerator;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> get(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Integer id) {
        items.remove(id);
    }

    @Override
    public List<Item> findAllByUser(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByText(String text) {
        return items.values().stream()
                .filter(item -> {
                    if (!item.getAvailable()) {
                        return false;
                    }
                    if (item.getName().toLowerCase().contains(text)) {
                        return true;
                    }
                    return item.getDescription().toLowerCase().contains(text);
                })
                .collect(Collectors.toList());
    }
}
