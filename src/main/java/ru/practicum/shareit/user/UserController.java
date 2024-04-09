package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Integer id) {
      log.info("Пришел запрос GET /users/{}", id);
      UserDto userDto = userService.getUser(id);
      log.info("Отправлен ответ GET /users/{}: {}",id, userDto);
      return userDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUser() {
        log.info("Пришел запрос GET /users");
        List<UserDto> userDtoList = userService.getAll();
        log.info("Отправлен ответ GET /users: {}", userDtoList);
        return userDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody @Valid UserDto userDto) {
        log.info("Пришел запрос POST /users: {}", userDto);
        UserDto newUserDto = userService.saveUser(userDto);
        log.info("Отправлен ответ POST /users: {}", newUserDto);
        return newUserDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
        log.info("Пришел запрос PATCH /users/{}", id);
        UserDto newUserDto = userService.updateUser(id, userDto);
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
