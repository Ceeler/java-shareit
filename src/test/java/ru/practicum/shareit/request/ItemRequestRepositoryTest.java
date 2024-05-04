package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User testUser;

    private Item testItem;

    private ItemRequest testItemRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Test")
                .email("test@mail.ru")
                .build();

        testItem = Item.builder()
                .name("test1")
                .description("test1 description")
                .available(true)
                .request(null)
                .owner(testUser)
                .build();

        testItemRequest = ItemRequest.builder()
                .description("test1")
                .author(testUser)
                .build();

        userRepository.save(testUser);
        itemRepository.save(testItem);
        itemRequestRepository.save(testItemRequest);
    }

    @Test
    void getAllByUserIdWithItemsShouldReturnRequest2Request1() {
        ItemRequest testItemRequest2 = ItemRequest.builder()
                .description("test1")
                .author(testUser)
                .build();
        itemRequestRepository.save(testItemRequest2);

        List<ItemRequest> requests = itemRequestRepository.getAllByUserIdWithItems(testUser.getId());

        assertEquals(2, requests.size());

        assertEquals(testItemRequest2.getId(), requests.get(0).getId());
        assertEquals(testItemRequest.getId(), requests.get(1).getId());
    }

    @Test
    void getAllWithItemsShouldReturnEmptyList() {
        ItemRequest testItemRequest2 = ItemRequest.builder()
                .description("test1")
                .author(testUser)
                .build();

        itemRequestRepository.save(testItemRequest2);

        Page<ItemRequest> requestsPage = itemRequestRepository.getAllWithItems(testUser.getId(), PageRequest.of(0, 20));

        List<ItemRequest> requests = requestsPage.getContent();

        assertEquals(0, requests.size());
    }

}
