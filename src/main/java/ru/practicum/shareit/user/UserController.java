package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable Integer id) {
      log.info("Пришел запрос GET /users/{}", id);
      UserResponse userDto = userService.getUser(id);
      log.info("Отправлен ответ GET /users/{}: {}",id, userDto);
      return userDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUser() {
        log.info("Пришел запрос GET /users");
        List<UserResponse> userDtoList = userService.getAll();
        log.info("Отправлен ответ GET /users: {}", userDtoList);
        return userDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse saveUser(@RequestBody @Valid UserCreateRequest userDto) {
        log.info("Пришел запрос POST /users: {}", userDto);
        UserResponse newUserDto = userService.saveUser(userDto);
        log.info("Отправлен ответ POST /users: {}", newUserDto);
        return newUserDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest userDto) {
        log.info("Пришел запрос PATCH /users/{}", id);
        UserResponse newUserDto = userService.updateUser(id, userDto);
        log.info("Отправлен ответ PATCH /users/{}: {}",id, newUserDto);
        return newUserDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришел запрос DELETE /users/{}", id);
        userService.deleteUser(id);
        log.info("Отправлен ответ DELETE /users/{}", id);
    }
}
