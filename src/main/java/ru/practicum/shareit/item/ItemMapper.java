package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;

import java.util.List;

public interface ItemMapper {

    Item toModel(ItemCreateRequest dto);

    List<ItemResponse> toDtoList(List<Item> items);

    ItemResponse toDto(Item model);

    void updateModelFromDto(Item model, ItemUpdateRequest itemDto);

}
