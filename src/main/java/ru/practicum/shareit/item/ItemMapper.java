package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.BaseMapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper implements BaseMapper<Item, ItemDto> {

    @Override
    public Item toModel(ItemDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    @Override
    public List<ItemDto> toDtoList(List<Item> items) {
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto toDto(Item model) {
        return ItemDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .build();
    }

    @Override
    public void updateModelFromDto(Item model, ItemDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            model.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            model.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            model.setAvailable(itemDto.getAvailable());
        }
    }
}
