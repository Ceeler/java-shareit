package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Integer id, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос GET /items/{} userId={}", id, userId);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItem(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос GET /items userId={}", userId);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemClient.getAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemCreateRequest itemDto,
                                           @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /items userId{}: {}", userId, itemDto);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemClient.saveItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Integer id,
                                             @RequestBody @Valid ItemUpdateRequest itemDto,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос PATCH /items/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemClient.updateItem(id, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Integer id,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос DELETE /items/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemClient.deleteItem(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Пришел запрос GET /items/search?text={}", text);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        return itemClient.findByText(text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestBody @Valid CommentRequest commentRequest,
            @PathVariable Integer id,
            @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        log.info("Пришел запрос POST /items/{}/comment userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        return itemClient.addComment(commentRequest, id, userId);
    }
}
