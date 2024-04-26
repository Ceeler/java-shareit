package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemGetResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {


    public static Item toModel(ItemCreateRequest dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }


    public static List<ItemResponse> toDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }


    public static ItemResponse toDto(Item model) {
        return ItemResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .build();
    }


    public static ItemGetResponse toItemGetDto(Item model, Booking last, Booking next) {
        return ItemGetResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .lastBooking(last == null ? null : BookingMapper.toBookingBookerDto(last))
                .nextBooking(next == null ? null : BookingMapper.toBookingBookerDto(next))
                .comments(CommentMapper.toDtoList(model.getComments()))
                .build();
    }


    public static void updateModelFromDto(Item model, ItemUpdateRequest itemDto) {
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


    public static ItemNameDto toItemNameDto(Item model) {
        return new ItemNameDto(model.getId(), model.getName());
    }

}
