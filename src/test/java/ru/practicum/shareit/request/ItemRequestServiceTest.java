package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestService itemRequestService;

    private ItemRequest request;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestService(itemRequestRepository, userRepository);

        request = ItemRequest.builder()
                .id(1)
                .description("test desc")
                .author(User.builder()
                        .id(1)
                        .name("Test")
                        .email("test@mail.ru")
                        .build())
                .createdAt(LocalDateTime.now())
                .items(List.of(Item.builder()
                        .id(1)
                        .name("test")
                        .available(true)
                        .request(ItemRequest.builder()
                                .id(2)
                                .build())
                        .build()))
                .build();
    }

    @Test
    void addItemRequestNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.addItemRequest(new ItemRequestRequest(), 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addItemRequestReturnRequestResponse() {
        ItemRequestRequest requestRequest = ItemRequestRequest.builder()
                .description("test desc")
                .build();

        Mockito
                .when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(request);

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).name("Test").build()));

        ItemRequestDto dto = itemRequestService.addItemRequest(requestRequest, 1);

        Assertions.assertEquals(request.getId(), dto.getId());
    }

    @Test
    void getOwnItemRequestNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getOwnItemRequest(1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getOwnItemRequestReturnRequestResponse() {
        Mockito
                .when(itemRequestRepository.getAllByUserIdWithItems(Mockito.anyInt()))
                .thenReturn(List.of(request));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).name("Test").build()));

        List<ItemRequestDto> dtos = itemRequestService.getOwnItemRequest(1);

        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(request.getId(), dtos.get(0).getId());
    }

    @Test
    void getAllItemRequestReturnRequestResponse() {
        Mockito
                .when(itemRequestRepository.getAllWithItems(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));

        List<ItemRequestDto> dtos = itemRequestService.getAllItemRequest(1, 0, 20);

        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(request.getId(), dtos.get(0).getId());
    }

    @Test
    void getItemRequestUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(1, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemRequestRequestNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).name("Test").build()));

        Mockito
                .when(itemRequestRepository.getByIdWithItems(Mockito.any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(1, 1));

        Assertions.assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void getItemRequestReturnRequestResponse() {
        Mockito
                .when(itemRequestRepository.getByIdWithItems(Mockito.any()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(User.builder().id(1).name("Test").build()));

        ItemRequestDto dto = itemRequestService.getItemRequest(1, 1);

        Assertions.assertEquals(request.getId(), dto.getId());
    }
}