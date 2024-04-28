package ru.practicum.shareit.item;

import ru.practicum.shareit.common.BaseDao;

import java.util.List;

@Deprecated
public interface ItemDao extends BaseDao<Item, Integer> {

    List<Item> findAllByUser(Integer userId);

    List<Item> findAllByText(String text);
}
