package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.NotAuthenticatedException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable Integer id) {
        log.info("Пришел запрос GET /items/{}", id);
        ItemDto itemDto = itemService.getItem(id);
        log.info("Отправлен ответ GET /items/{}: {}",id, itemDto);
        return itemDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItem(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос GET /items userId={}", userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        List<ItemDto> itemDtoList = itemService.getAll(userId);
        log.info("Отправлен ответ GET /items: {}", itemDtoList);
        return itemDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto saveItem(@RequestBody @Valid ItemDto itemDto,
                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос POST /items userId{}: {}", userId, itemDto);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemDto newItemDto = itemService.saveItem(itemDto, userId);
        log.info("Отправлен ответ POST /items: {}", newItemDto);
        return newItemDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@PathVariable Integer id,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Пришел запрос PATCH /items/{} userId={}", id, userId);
        if (userId == null) {
            throw new NotAuthenticatedException("Header X-Sharer-User-Id requested");
        }
        ItemDto newItemDto = itemService.updateItem(id, itemDto, userId);
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
    public List<ItemDto> getItem(@RequestParam String text) {
        log.info("Пришел запрос GET /items/search?text={}", text);
        List<ItemDto> itemDtoList = itemService.findByText(text);
        log.info("Отправлен ответ GET /items/search: {}", itemDtoList);
        return itemDtoList;
    }
}
