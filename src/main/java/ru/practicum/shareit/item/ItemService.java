package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.comment.dto.response.CommentResponse;
import ru.practicum.shareit.exception.model.NotAuthorizedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemGetResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;


    public ItemGetResponse getItem(Integer id, Integer userId) {
        Item item = itemRepository.getItemByIdWithOwner(id).orElseThrow(() -> new NotFoundException("Item not found"));
        if (userId != null) {
            getUser(userId);
            if (item.getOwner().getId().equals(userId)) {
                return getDtoWithBookingFromItem(item, LocalDateTime.now());
            }
        }
        return ItemMapper.toItemGetDto(item, null, null);
    }

    public List<ItemGetResponse> getAll(Integer userId) {
        getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        List<ItemGetResponse> response = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            response.add(getDtoWithBookingFromItem(item, now));
        }
        return response;
    }

    public ItemResponse saveItem(ItemCreateRequest itemDto, Integer userId) {
        Item newItem = ItemMapper.toModel(itemDto);
        User user = getUser(userId);
        newItem.setOwner(user);
        Item item = itemRepository.save(newItem);
        return ItemMapper.toDto(item);
    }

    public ItemResponse updateItem(Integer id, ItemUpdateRequest itemDto, Integer userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't change item");
        }
        getUser(userId);

        ItemMapper.updateModelFromDto(item, itemDto);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    public void deleteItem(Integer id, Integer userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("You can't change item");
        }
        getUser(userId);
        itemRepository.deleteById(id);
    }

    public List<ItemResponse> findByText(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return ItemMapper.toDtoList(itemRepository.findAllByDescriptionOrNameContainsIgnoreCase(text.trim()));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private ItemGetResponse getDtoWithBookingFromItem(Item item, LocalDateTime now) {
        List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
        if (!bookings.isEmpty()) {
            Booking last = null;
            Booking next = null;

            for (int i = 0; i < bookings.size(); i++) {
                Booking currentBook = bookings.get(i);
                if (currentBook.getEnd().isAfter(now)) {
                    if (currentBook.getStart().isBefore(now)) {
                        last = bookings.get(i);

                        if (i + 1 < bookings.size()) {
                            next = bookings.get(i - 1);
                        }

                    } else {
                        if (i - 1 >= 0) {
                            last = bookings.get(i - 1);
                        }
                        next = bookings.get(i);
                    }
                    break;
                }
            }

            return ItemMapper.toItemGetDto(item, last, next);
        } else {
            return ItemMapper.toItemGetDto(item, null, null);
        }
    }

    public CommentResponse addComment(CommentRequest dto, Integer itemId, Integer userId) {
        User user = getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (bookingRepository.countByBookerIdAndItemId(userId, itemId, LocalDateTime.now()) < 1) {
            throw new IllegalArgumentException("You have to rent item, before comment");
        }
        Comment comment = CommentMapper.toModel(dto);
        comment.setAuthor(user);
        comment.setItem(item);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }
}
