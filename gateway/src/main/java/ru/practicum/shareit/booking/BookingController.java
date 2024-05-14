package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBook(
            @PathVariable Integer id,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /bookings/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return bookingClient.getBooking(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> bookItem(
            @RequestBody @Valid BookingCreateRequest bookingDto,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /bookings userId={}: {}", userId, bookingDto);

        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }

        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> approveBook(
            @PathVariable Integer id,
            @RequestParam(required = false) Boolean approved,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId
    ) {
        log.info("Пришел запрос PATCH /bookings/{}?approved={} userId={}", id, approved, userId);

        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }

        if (approved == null) {
            throw new IllegalArgumentException("Approved must be specified");
        }

        return bookingClient.approveBookingStatus(id, userId, approved);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        log.info("Пришел запрос GET /bookings?state={} userId={}", stateStr, userId);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        State state = State.from(stateStr).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + stateStr)
        );

        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserItemsBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        log.info("Пришел запрос GET /bookings/owner?state={} userId={}", stateStr, userId);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        State state = State.from(stateStr).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + stateStr)
        );

        return bookingClient.getUserItemsBookings(userId, state, from, size);
    }

}
