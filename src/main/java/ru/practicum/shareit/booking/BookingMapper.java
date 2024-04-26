package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toModel(BookingCreateRequest dto) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(Item.builder().id(dto.getItemId()).build())
                .status(Status.WAITING)
                .build();
    }

    public static BookingResponse toDto(Booking model) {
        return BookingResponse.builder()
                .id(model.getId())
                .start(model.getStart())
                .end(model.getEnd())
                .item(ItemMapper.toItemNameDto(model.getItem()))
                .booker(UserMapper.toDto(model.getBooker()))
                .status(model.getStatus())
                .build();
    }

    public static BookingBookerDto toBookingBookerDto(Booking booking) {
        return BookingBookerDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();
    }

}

