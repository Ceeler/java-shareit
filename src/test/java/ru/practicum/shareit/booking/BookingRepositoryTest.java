package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;
    private Booking testBooking;

    private User testUser;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("test1")
                .email("test1@mail.ru")
                .build();

        User testUser2 = User.builder()
                .name("test2")
                .email("test2@mail.ru")
                .build();

        testItem = Item.builder()
                .name("testItem")
                .description("testItemDescr")
                .available(true)
                .owner(testUser)
                .request(null)
                .build();

        Item testItem2 = Item.builder()
                .name("testItem2")
                .description("testItemDescr2")
                .available(true)
                .owner(testUser2)
                .request(null)
                .build();

        userRepository.save(testUser);
        userRepository.save(testUser2);

        itemRepository.save(testItem);
        itemRepository.save(testItem2);

        testBooking = Booking.builder()
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .booker(testUser)
                .item(testItem)
                .status(Status.APPROVED)
                .build();

        Booking testBooking2 = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(testUser2)
                .item(testItem2)
                .status(Status.APPROVED)
                .build();

        bookingRepository.save(testBooking);
        bookingRepository.save(testBooking2);
    }

    @Test
    void findByIdWithBookerAndItemShouldReturnFilledOptional() {
        Booking booking = bookingRepository.findByIdWithBookerAndItem(testBooking.getId()).orElse(null);

        assertNotNull(booking);
        assertEquals(testBooking.getStart(), booking.getStart());
        assertEquals(testBooking.getEnd(), booking.getEnd());
        assertEquals(testBooking.getBooker().getId(), booking.getBooker().getId());
        assertEquals(testBooking.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void findAllByItem_IdShouldReturnOneAPPROVEDBooking() {
        Booking testBooking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .booker(testUser)
                .item(testItem)
                .status(Status.REJECTED)
                .build();

        bookingRepository.save(testBooking2);

        List<Booking> bookings = bookingRepository.findAllByItem_Id(testItem.getId());

        assertEquals(1, bookings.size());
        Booking booking = bookings.get(0);
        assertEquals(testBooking.getStart(), booking.getStart());
        assertEquals(testBooking.getEnd(), booking.getEnd());
    }

    @Test
    void countByBookerIdAndItemIdShouldReturn1() {
        Integer count = bookingRepository.countByBookerIdAndItemId(testUser.getId(), testItem.getId(), LocalDateTime.now());

        assertEquals(1, count);
    }

}
