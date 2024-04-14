package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item toModel(ItemCreateRequest dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    @Override
    public List<ItemResponse> toDtoList(List<Item> items) {
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemResponse toDto(Item model) {
        return ItemResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .build();
    }

    @Override
    public void updateModelFromDto(Item model, ItemUpdateRequest itemDto) {
        if (itemDto.getName() != null) {
            model.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            model.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            model.setAvailable(itemDto.getAvailable());
        }
    }
}
