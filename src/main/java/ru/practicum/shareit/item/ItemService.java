package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;

    private final UserDao userDao;

    public ItemService(@Qualifier("ItemMemoryDao") ItemDao dao,
                       ItemMapper itemMapper,
                       UserDao userDao) {
        this.itemDao = dao;
        this.itemMapper = itemMapper;
        this.userDao = userDao;
    }

    public ItemDto getItem(Integer id) {
        Item item = itemDao.get(id).orElseThrow(() -> new NotFoundException("Item not found"));
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAll(Integer userId) {
        getUser(userId);
        return itemMapper.toDtoList(itemDao.findAllByUser(userId));
    }

    public ItemDto saveItem(ItemDto itemDto, Integer userId) {
        Item newItem = itemMapper.toModel(itemDto);
        User user = getUser(userId);
        newItem.setOwner(user);
        Item item = itemDao.save(newItem);
        return itemMapper.toDto(item);
    }

    public ItemDto updateItem(Integer id, ItemDto itemDto, Integer userId) {
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

    public List<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemMapper.toDtoList(itemDao.findAllByText(text.toLowerCase().strip()));
    }

    private User getUser(Integer userId) {
        return userDao.get(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
