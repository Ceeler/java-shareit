package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestRequest itemRequestRequest,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /requests userId={}", userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemRequestClient.addItemRequest(itemRequestRequest, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /requests userId={}", userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemRequestClient.getOwnItemRequest(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests(
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
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequest(@PathVariable Integer id,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /requests/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemRequestClient.getItemRequest(id, userId);
    }

}
