package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.comment.dto.response.CommentResponse;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemGetResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemService itemService;

    private final Item item = Item.builder()
            .id(1)
            .name("test name")
            .description("test description")
            .available(true)
            .owner(User.builder()
                    .id(1)
                    .name("Test")
                    .email("test@mail.ru")
                    .build())
            .comments(new ArrayList<>())
            .build();

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getItemItemNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.getItemByIdWithOwner(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1, 1));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void getItemWithoutUserShouldCallBooking0Time() {
        Mockito
                .when(itemRepository.getItemByIdWithOwner(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item()));

        itemService.getItem(1, null);

        Mockito.verify(bookingRepository, Mockito.never())
                .findAllByItem_Id(Mockito.anyInt());
    }

    @Test
    void getItemUserNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.getItemByIdWithOwner(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemByNotOwnerShouldCallBooking0Time() {
        Mockito
                .when(itemRepository.getItemByIdWithOwner(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .owner(User.builder()
                                .id(1)
                                .build())
                        .build()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        itemService.getItem(1, 2);

        Mockito.verify(bookingRepository, Mockito.never())
                .findAllByItem_Id(Mockito.anyInt());
    }

    @Test
    void getItemByOwnerShouldCallBooking1Time() {
        User user = User.builder()
                .id(1)
                .name("Test")
                .build();

        Mockito
                .when(itemRepository.getItemByIdWithOwner(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(user)
                        .build()));

        List<Booking> bookings = List.of(
                Booking.builder()
                        .id(1)
                        .booker(user)
                        .start(LocalDateTime.now().minusHours(5))
                        .end(LocalDateTime.now().minusHours(6))
                        .build(),
                Booking.builder()
                        .id(2)
                        .booker(user)
                        .start(LocalDateTime.now().minusHours(2))
                        .end(LocalDateTime.now().minusHours(1))
                        .build(),
                Booking.builder()
                        .id(3)
                        .booker(user)
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusHours(2))
                        .build()
        );

        Mockito
                .when(bookingRepository.findAllByItem_Id(Mockito.anyInt()))
                .thenReturn(bookings);

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        ItemGetResponse response = itemService.getItem(1, 1);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItem_Id(Mockito.anyInt());

        Assertions.assertEquals(response.getNextBooking().getId(), 3);
        Assertions.assertEquals(response.getLastBooking().getId(), 2);
    }

    @Test
    void getAllUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getAll(1, 1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getAllShouldCallBookingRepository0Time() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        Mockito
                .when(itemRepository.findAllByOwnerIdOrderById(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        itemService.getAll(1, 1, 1);

        Mockito.verify(bookingRepository, Mockito.never())
                .findAllByItem_Id(Mockito.anyInt());
    }

    @Test
    void getAllShouldCallBookingRepository1TimeForEachItem() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        Mockito
                .when(itemRepository.findAllByOwnerIdOrderById(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(Item.builder().id(1).build(), Item.builder().id(2).build())));

        itemService.getAll(1, 1, 1);

        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItem_Id(Mockito.anyInt());
    }

    @Test
    void saveItemUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.saveItem(new ItemCreateRequest(), 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void saveItemWithNullRequestShouldCallItemRequestRepository0TimeAndSaveItem() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(Item.builder().id(1).build());

        ItemCreateRequest itemDto = ItemCreateRequest.builder()
                .requestId(null)
                .build();

        itemService.saveItem(itemDto, 1);

        Mockito.verify(itemRequestRepository, Mockito.never())
                .findById(Mockito.anyInt());

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void saveItemWithWrongRequestShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        Mockito
                .when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        ItemCreateRequest itemDto = ItemCreateRequest.builder()
                .requestId(99)
                .build();

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.saveItem(itemDto, 1));

        Assertions.assertEquals("Request not found", exception.getMessage());

        Mockito.verify(itemRepository, Mockito.never())
                .save(Mockito.any(Item.class));
    }

    @Test
    void saveItemWithRequestShouldCallItemRequestRepository1TimeAndSave() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User()));

        Mockito
                .when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(new ItemRequest()));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(Item.builder().id(1).build());

        ItemCreateRequest itemDto = ItemCreateRequest.builder()
                .requestId(1)
                .build();

        itemService.saveItem(itemDto, 1);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(Mockito.anyInt());

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(Mockito.any(Item.class));
    }


    @Test
    void updateItemItemNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1, new ItemUpdateRequest(), 1));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void updateItemByNotOwnerShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder().id(1).build()).build()));

        final NotAuthorizedException exception = Assertions.assertThrows(
                NotAuthorizedException.class,
                () -> itemService.updateItem(1, new ItemUpdateRequest(), 2));

        Assertions.assertEquals("You can't change item", exception.getMessage());
    }

    @Test
    void updateItemUserNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder().id(1).build()).build()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(1, new ItemUpdateRequest(), 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateIteShouldReturnItem() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).build()));

        ItemResponse response = itemService.updateItem(1, new ItemUpdateRequest(), 1);

        Assertions.assertEquals(item.getId(), response.getId());
        Assertions.assertEquals(item.getName(), response.getName());
    }

    @Test
    void deleteItemItemNotFoundShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.deleteItem(1, 1));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void deleteItemByNotOwnerShouldThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder().id(1).build()).build()));

        final NotAuthorizedException exception = Assertions.assertThrows(
                NotAuthorizedException.class,
                () -> itemService.deleteItem(1, 2));

        Assertions.assertEquals("You can't change item", exception.getMessage());
    }

    @Test
    void deleteItemShouldNotThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder().id(1).build()).build()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).build()));

        itemService.deleteItem(1, 1);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(Mockito.anyInt());
    }

    @Test
    void findByTextWithBlankTextShouldReturnEmptyArray() {
        List<ItemResponse> items = itemService.findByText(" ", 0, 20);
        Assertions.assertEquals(0, items.size());
    }

    @Test
    void findByTextWitTextShouldReturnArray() {
        Mockito
                .when(itemRepository.findAllByDescriptionOrNameContainsIgnoreCase(Mockito.anyString(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemResponse> items = itemService.findByText("test", 0, 20);

        Assertions.assertEquals(1, items.size());

        ItemResponse response = items.get(0);

        Assertions.assertEquals(item.getId(), response.getId());
        Assertions.assertEquals(item.getName(), response.getName());
    }

    @Test
    void addCommentNotRentedThrowException() {
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder().id(1).build()).build()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).build()));

        Mockito
                .when(bookingRepository.countByBookerIdAndItemId(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(0);

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> itemService.addComment(new CommentRequest(), 1, 1));

        Assertions.assertEquals("You have to rent item, before comment", exception.getMessage());
    }

    @Test
    void addCommentShouldReturnCommentResponse() {
        CommentRequest comment = new CommentRequest("test text");

        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(Item.builder().owner(User.builder()
                        .id(1)
                        .build()).build()));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).name("Test").build()));

        Mockito
                .when(bookingRepository.countByBookerIdAndItemId(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(1);

        CommentResponse response = itemService.addComment(comment, 1, 1);

        Assertions.assertEquals(comment.getText(), response.getText());
        Assertions.assertEquals("Test", response.getAuthorName());

    }

}