package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestRequest itemRequestRequest,
                                     @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /requests userId={}", userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemRequestDto response = itemRequestService.addItemRequest(itemRequestRequest, userId);
        log.info("Отправлен ответ POST /requests userId={} :{}", userId, response);
        return response;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /requests userId={}", userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        List<ItemRequestDto> response = itemRequestService.getOwnItemRequest(userId);
        log.info("Отправлен ответ GET /requests userId={} : {}", userId, response);
        return response;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        log.info("Пришел запрос GET /requests/all?from={}&size={}", from, size);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        List<ItemRequestDto> response = itemRequestService.getAllItemRequest(userId, from, size);
        log.info("Отправлен ответ GET /requests/all : {}", response);
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequest(@PathVariable Integer id,
                                     @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /requests/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemRequestDto response = itemRequestService.getItemRequest(id, userId);
        log.info("Отправлен ответ GET /requests/{} userId={} : {}", id, userId, response);
        return response;
    }

}
