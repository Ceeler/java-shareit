package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.request.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.response.BookingResponse;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


    public BookingResponse bookItem(Integer userId, BookingCreateRequest dto) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item not available");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!isItemFree(dto.getItemId(), dto.getStart(), dto.getEnd())) {
            throw new IllegalArgumentException("Already booked for this  time");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("You can't book your item");
        }
        Booking booking = BookingMapper.toModel(dto, user, item);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingResponse approveBookingStatus(Integer id, Integer userId, boolean isApproved) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingRepository.findByIdWithBookerAndItem(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't change this booking");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new IllegalArgumentException("Already approved");
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    public BookingResponse getBooking(Integer id, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Booking booking = bookingRepository.findByIdWithBookerAndItem(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't get this booking");
        }

        return BookingMapper.toDto(booking);
    }

    public List<BookingResponse> getUserBookings(Integer userId, State state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        BooleanExpression expression = QBooking.booking.booker.id.eq(userId);
        expression = addStateFilterPredicate(expression, state);
        Iterable<Booking> bookings = bookingRepository.findAll(expression, QBooking.booking.start.desc());
        return StreamSupport.stream(bookings.spliterator(), false)
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getUserItemsBookings(Integer userId, State state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        BooleanExpression expression = QBooking.booking.item.owner.id.eq(userId);
        expression = addStateFilterPredicate(expression, state);
        Iterable<Booking> bookings = bookingRepository.findAll(expression, QBooking.booking.start.desc());
        return StreamSupport.stream(bookings.spliterator(), false)
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private BooleanExpression addStateFilterPredicate(BooleanExpression stateExpression, State state) {
        switch (state) {
            case CURRENT:
                stateExpression = stateExpression.and(QBooking.booking.start.before(LocalDateTime.now())
                        .and(QBooking.booking.end.after(LocalDateTime.now())));
                break;
            case PAST:
                stateExpression = stateExpression.and(QBooking.booking.start.before(LocalDateTime.now())
                        .and(QBooking.booking.end.before(LocalDateTime.now())));
                break;
            case FUTURE:
                stateExpression = stateExpression.and(QBooking.booking.start.after(LocalDateTime.now()));
                break;
            case WAITING:
                stateExpression = stateExpression.and(QBooking.booking.status.eq(Status.WAITING));
                break;
            case REJECTED:
                stateExpression = stateExpression.and(QBooking.booking.status.eq(Status.REJECTED));
                break;
        }
        return stateExpression;
    }

    private boolean isItemFree(Integer itemId, LocalDateTime start, LocalDateTime end) {
        BooleanExpression expression = addStateFilterPredicate(QBooking.booking.item.id.eq(itemId), State.FUTURE);
        expression = expression.and(QBooking.booking.status.ne(Status.REJECTED));
        Iterable<Booking> bookings = bookingRepository.findAll(expression);
        for (Booking book : bookings) {
            if (start.isAfter(book.getStart()) && start.isBefore(book.getEnd())) {
                return false;
            }
            if (end.isAfter(book.getStart()) && end.isBefore(book.getEnd())) {
                return false;
            }
        }
        return true;
    }
}
