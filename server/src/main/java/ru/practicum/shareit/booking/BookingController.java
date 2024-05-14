package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.enums.State;

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
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /bookings/{} userId={}", id, userId);
        BookingResponse booking = bookingService.getBooking(id, userId);
        log.info("Отправлен ответ GET /bookings/{}: {}", id, booking);
        return booking;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookItem(
            @RequestBody BookingCreateRequest bookingDto,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /bookings userId={}: {}", userId, bookingDto);
        BookingResponse booking = bookingService.bookItem(userId, bookingDto);
        log.info("Отправлен ответ POST /bookings: {}", booking);
        return booking;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse approveBook(
            @PathVariable Integer id,
            @RequestParam(required = false) Boolean approved,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId
    ) {
        log.info("Пришел запрос PATCH /bookings/{}?approved={} userId={}", id, approved, userId);
        BookingResponse booking = bookingService.approveBookingStatus(id, userId, approved);
        log.info("Отправлен ответ PATCH /bookings/{}?approved={}: {}", id, approved, booking);
        return booking;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        log.info("Пришел запрос GET /bookings?state={} userId={}", stateStr, userId);
        State state = State.valueOf(stateStr.toUpperCase());
        List<BookingResponse> bookings = bookingService.getUserBookings(userId, state, from, size);
        log.info("Отправлен ответ GET /bookings?state={} userId={}: {}", state, userId, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserItemsBookings(
            @RequestParam(defaultValue = "ALL", value = "state") String stateStr,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        log.info("Пришел запрос GET /bookings/owner?state={} userId={}", stateStr, userId);
        State state = State.valueOf(stateStr.toUpperCase());
        List<BookingResponse> bookings = bookingService.getUserItemsBookings(userId, state, from, size);
        log.info("Отправлен ответ GET /bookings/owner?state={} userId={}: {}", state, userId, bookings);
        return bookings;
    }

}
