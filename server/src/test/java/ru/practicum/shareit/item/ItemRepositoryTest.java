package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    private User testUser;

    private Item testItem;

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

        userRepository.save(testUser);
        itemRepository.save(testItem);
    }

    @Test
    void findAllByDescriptionOrNameContainsIgnoreCaseShouldReturn1() {
        Item item1notAvailable = Item.builder()
                .name("test1")
                .description("test1 description")
                .available(false)
                .request(null)
                .owner(testUser)
                .build();

        Item item2diffName = Item.builder()
                .name("test2")
                .description("test2 description")
                .available(true)
                .request(null)
                .owner(testUser)
                .build();

        itemRepository.save(item1notAvailable);
        itemRepository.save(item2diffName);

        Page<Item> pageItem = itemRepository.findAllByDescriptionOrNameContainsIgnoreCase("test1", PageRequest.of(0, 20));

        List<Item> items = pageItem.getContent();

        assertEquals(1, items.size());

        Item item = items.get(0);

        assertEquals(item.getId(), testItem.getId());
        assertEquals(item.getName(), testItem.getName());
        assertEquals(item.getDescription(), testItem.getDescription());
    }


}
