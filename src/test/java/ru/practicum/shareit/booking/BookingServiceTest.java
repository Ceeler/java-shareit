package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private BookingService bookingService;

    private BookingCreateRequest bookingDto;

    private Booking booking;

    private final Item item = Item.builder()
            .id(1)
            .name("test")
            .available(true)
            .owner(User.builder()
                    .id(2)
                    .name("Tes2t")
                    .email("test2@mail.ru")
                    .build())
            .build();

    private final User user = User.builder()
            .id(1)
            .name("Test")
            .email("test@mail.ru")
            .build();

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
        bookingDto = BookingCreateRequest.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .itemId(1)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void bookItemItemNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.bookItem(1, bookingDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void bookItemByOwnerShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(1).build())
                        .build()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.bookItem(1, bookingDto));

        Assertions.assertEquals("You can't book your item", exception.getMessage());
    }

    @Test
    void bookItemItemNotAvailableShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(2).build())
                        .available(false)
                        .build()));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.bookItem(1, bookingDto));

        Assertions.assertEquals("Item not available", exception.getMessage());
    }

    @Test
    void bookItemUserNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(2).build())
                        .available(true)
                        .build()));
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.bookItem(1, bookingDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void bookItemShouldReturnBookingResponse() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(12))
                        .build()));

        Mockito
                .when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);

        BookingResponse response = bookingService.bookItem(1, bookingDto);

        Assertions.assertEquals(response.getId(), item.getId());
        Assertions.assertEquals(response.getBooker().getId(), user.getId());
        Assertions.assertEquals(response.getItem().getId(), item.getId());
    }

    @Test
    void bookItemAlreadyBookedShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder().id(2).build())
                        .available(true)
                        .build()));
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findAll(Mockito.any(BooleanExpression.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .build()));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.bookItem(1, bookingDto));

        Assertions.assertEquals("Already booked for this time", exception.getMessage());
    }

    @Test
    void approveBookingStatusUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBookingStatus(1, 1, true));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void approveBookingItemNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBookingStatus(1, 1, true));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void approveBookingByNotOwnerShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.of(Booking.builder()
                        .item(Item.builder()
                                .owner(User.builder()
                                        .id(2)
                                        .build())
                                .build())
                        .build()));

        final NotAuthorizedException exception = Assertions.assertThrows(
                NotAuthorizedException.class,
                () -> bookingService.approveBookingStatus(1, 1, true));

        Assertions.assertEquals("You can't change this booking", exception.getMessage());
    }

    @Test
    void approveBookingAlreadyApprovedShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.of(Booking.builder()
                        .item(Item.builder()
                                .owner(User.builder()
                                        .id(1)
                                        .build())
                                .build())
                        .status(Status.APPROVED)
                        .build()));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.approveBookingStatus(1, 1, true));

        Assertions.assertEquals("Already approved", exception.getMessage());
    }

    @Test
    void approveBookingShouldReturnBookingResponse() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.approveBookingStatus(1, 2, true);

        Assertions.assertEquals(Status.APPROVED, response.getStatus());
    }

    @Test
    void getBookingUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getBookingItemNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingByNotOwnerShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().build()));

        Mockito
                .when(bookingRepository.findByIdWithBookerAndItem(Mockito.anyInt()))
                .thenReturn(Optional.of(Booking.builder()
                        .item(Item.builder()
                                .owner(User.builder()
                                        .id(2)
                                        .build())
                                .build())
                        .booker(User.builder()
                                .id(3)
                                .build())
                        .status(Status.APPROVED)
                        .build()));

        final NotAuthorizedException exception = Assertions.assertThrows(
                NotAuthorizedException.class,
                () -> bookingService.getBooking(1, 1));

        Assertions.assertEquals("You can't get this booking", exception.getMessage());
    }

    @Test
    void getUserBookingsUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserBookings(1, State.ALL, 1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserBookingsReturnBookingResponse() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponse> responses = bookingService.getUserBookings(1, State.ALL, 1, 1);

        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(booking.getId(), responses.get(0).getId());
    }

    @Test
    void getUserItemsBookingsUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserItemsBookings(1, State.CURRENT, 1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserItemsBookingsReturnBookingResponse() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponse> responses = bookingService.getUserItemsBookings(1, State.WAITING, 1, 1);

        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(booking.getId(), responses.get(0).getId());
    }
}
