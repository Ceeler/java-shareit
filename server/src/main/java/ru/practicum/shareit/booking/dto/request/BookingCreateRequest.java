package ru.practicum.shareit.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@StartBeforeEnd
@Builder
public class BookingCreateRequest {

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @NotNull
    private Integer itemId;
}
