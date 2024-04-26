package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.comment.dto.response.CommentResponse;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemGetResponse {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private BookingBookerDto nextBooking;

    private BookingBookerDto lastBooking;

    private List<CommentResponse> comments;
}
