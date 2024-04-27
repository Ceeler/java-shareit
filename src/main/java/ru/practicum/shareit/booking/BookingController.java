package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBook(
            @PathVariable Integer id,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос GET /bookings/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        BookingResponse booking = bookingService.getBooking(id, userId);
        log.info("Отправлен ответ GET /bookings/{}: {}", id, booking);
        return booking;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookItem(
            @RequestBody @Valid BookingCreateRequest bookingDto,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос POST /bookings userId={}: {}", userId, bookingDto);

        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }

        BookingResponse booking = bookingService.bookItem(userId, bookingDto);
        log.info("Отправлен ответ POST /bookings: {}", booking);
        return booking;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse approveBook(
            @PathVariable Integer id,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("Пришел запрос PATCH /bookings/{}?approved={} userId={}", id, approved, userId);

        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }

        if (approved == null) {
            throw new IllegalArgumentException("Approved must be specified");
        }

        BookingResponse booking = bookingService.approveBookingStatus(id, userId, approved);
        log.info("Отправлен ответ PATCH /bookings/{}?approved={}: {}", id, approved, booking);

        return booking;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("Пришел запрос GET /bookings?state={} userId={}", stateStr, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        State state;
        try {
            state = State.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stateStr);
        }
        List<BookingResponse> bookings = bookingService.getUserBookings(userId, state);
        log.info("Отправлен ответ GET /bookings?state={} userId={}: {}", state, userId, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserItemsBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        log.info("Пришел запрос GET /bookings/owner?state={} userId={}", stateStr, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        State state;
        try {
            state = State.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stateStr);
        }
        List<BookingResponse> bookings = bookingService.getUserItemsBookings(userId, state);
        log.info("Отправлен ответ GET /bookings/owner?state={} userId={}: {}", state, userId, bookings);
        return bookings;
    }

}
