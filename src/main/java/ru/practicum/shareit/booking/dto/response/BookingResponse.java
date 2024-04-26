package ru.practicum.shareit.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemNameDto item;

    private UserResponse booker;

    private Status status;

}
