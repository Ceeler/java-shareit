package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    private ItemRequestMapper() {

    }

    public static ItemRequest toModel(ItemRequestRequest dto, User user) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .author(user)
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest model) {

        return ItemRequestDto.builder()
                .id(model.getId())
                .description(model.getDescription())
                .created(model.getCreatedAt())
                .items(model.getItems() != null ?
                        ItemMapper.toItemForItemRequestDtoList(model.getItems()) :
                        Collections.emptyList())
                .build();
    }

    public static List<ItemRequestDto> toDtoList(List<ItemRequest> requests) {
        return requests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}
