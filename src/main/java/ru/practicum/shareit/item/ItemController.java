package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.request.CommentRequest;
import ru.practicum.shareit.comment.dto.response.CommentResponse;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.item.dto.request.ItemCreateRequest;
import ru.practicum.shareit.item.dto.request.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.response.ItemGetResponse;
import ru.practicum.shareit.item.dto.response.ItemResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemGetResponse getItem(@PathVariable Integer id, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос GET /items/{} userId={}", id, userId);
        ItemGetResponse itemDto = itemService.getItem(id, userId);
        log.info("Отправлен ответ GET /items/{}: {}",id, itemDto);
        return itemDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemGetResponse> getAllItem(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос GET /items userId={}", userId);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        List<ItemGetResponse> itemDtoList = itemService.getAll(userId, from, size);
        log.info("Отправлен ответ GET /items: {}", itemDtoList);
        return itemDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse saveItem(@RequestBody @Valid ItemCreateRequest itemDto,
                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос POST /items userId{}: {}", userId, itemDto);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemResponse newItemDto = itemService.saveItem(itemDto, userId);
        log.info("Отправлен ответ POST /items: {}", newItemDto);
        return newItemDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse updateItem(@PathVariable Integer id,
                                   @RequestBody @Valid ItemUpdateRequest itemDto,
                                   @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос PATCH /items/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemResponse newItemDto = itemService.updateItem(id, itemDto, userId);
        log.info("Отправлен ответ PATCH /items/{}: {}",id, newItemDto);
        return newItemDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Integer id, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос DELETE /items/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        itemService.deleteItem(id, userId);
        log.info("Отправлен ответ DELETE /items/{}", id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getItem(@RequestParam String text,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Пришел запрос GET /items/search?text={}", text);
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Page must be from>0 and size>1");
        }
        List<ItemResponse> itemDtoList = itemService.findByText(text, from, size);
        log.info("Отправлен ответ GET /items/search: {}", itemDtoList);
        return itemDtoList;
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse addComment(
            @RequestBody @Valid CommentRequest commentRequest,
            @PathVariable Integer id,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос POST /items/{}/comment userId={}", id, userId);
        CommentResponse response = itemService.addComment(commentRequest, id, userId);
        log.info("Отправлен ответ POST /items/{}/comment userId={} :{}", id, userId, response);
        return response;
    }
}
