package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Integer id) {
        log.info("Пришел запрос GET /users/{}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        log.info("Пришел запрос GET /users");
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserCreateRequest userDto) {
        log.info("Пришел запрос POST /users: {}", userDto);
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @RequestBody @Valid UserUpdateRequest userDto) {
        log.info("Пришел запрос PATCH /users/{}", id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        log.info("Пришел запрос DELETE /users/{}", id);
        return userClient.deleteUser(id);
    }
}
