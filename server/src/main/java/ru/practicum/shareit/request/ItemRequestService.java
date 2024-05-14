package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    public ItemRequestDto addItemRequest(ItemRequestRequest dto, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest newItemRequest = ItemRequestMapper.toModel(dto, user);
        ItemRequest itemRequest = itemRequestRepository.save(newItemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    public List<ItemRequestDto> getOwnItemRequest(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ItemRequest> requests = itemRequestRepository.getAllByUserIdWithItems(userId);
        return ItemRequestMapper.toDtoList(requests);
    }

    public List<ItemRequestDto> getAllItemRequest(Integer userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ItemRequest> requestPage = itemRequestRepository.getAllWithItems(userId, page);
        return ItemRequestMapper.toDtoList(requestPage.getContent());
    }

    public ItemRequestDto getItemRequest(Integer id, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.getByIdWithItems(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        return ItemRequestMapper.toDto(itemRequest);
    }
}
