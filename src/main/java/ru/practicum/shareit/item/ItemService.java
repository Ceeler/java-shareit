package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemDaoImpl itemDao;

    private final UserDao userDao;

    private final ItemMapper itemMapper;

    public ItemResponse getItem(Integer id) {
        Item item = itemDao.get(id).orElseThrow(() -> new NotFoundException("Item not found"));
        return itemMapper.toDto(item);
    }

    public List<ItemResponse> getAll(Integer userId) {
        getUser(userId);
        return itemMapper.toDtoList(itemDao.findAllByUser(userId));
    }

    public ItemResponse saveItem(ItemCreateRequest itemDto, Integer userId) {
        Item newItem = itemMapper.toModel(itemDto);
        User user = getUser(userId);
        newItem.setOwner(user);
        Item item = itemDao.save(newItem);
        return itemMapper.toDto(item);
    }

    public ItemResponse updateItem(Integer id, ItemUpdateRequest itemDto, Integer userId) {
        Item item = itemDao.get(id).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't change item");
        }
        getUser(userId);

        itemMapper.updateModelFromDto(item, itemDto);
        return itemMapper.toDto(itemDao.update(item));
    }

    public void deleteItem(Integer id, Integer userId) {
        Item item = itemDao.get(id).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't change item");
        }
        getUser(userId);
        itemDao.delete(id);
    }

    public List<ItemResponse> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.toDtoList(itemDao.findAllByText(text.toLowerCase().strip()));
    }

    private User getUser(Integer userId) {
        return userDao.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
