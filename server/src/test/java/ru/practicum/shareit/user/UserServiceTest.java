package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        user = User.builder()
                .id(1)
                .name("Test")
                .email("test@mail.ru")
                .build();
    }

    @Test
    void getUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserReturnUserResponse() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        UserResponse response = userService.getUser(1);

        Assertions.assertEquals(user.getId(), response.getId());
        Assertions.assertEquals(user.getName(), response.getName());
    }

    @Test
    void getAllReturnUserResponse() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAll();

        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(user.getId(), responses.get(0).getId());
        Assertions.assertEquals(user.getName(), responses.get(0).getName());
    }

    @Test
    void saveUserReturnUserResponse() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("Test")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        UserResponse response = userService.saveUser(request);

        Assertions.assertEquals(user.getId(), response.getId());
        Assertions.assertEquals(user.getName(), response.getName());
    }

    @Test
    void updateUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(1, new UserUpdateRequest()));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserReturnUserResponse() {
        UserUpdateRequest request = new UserUpdateRequest("Test2", "test2@mail.ru");

        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        UserResponse response = userService.updateUser(1, request);

        Assertions.assertEquals(user.getId(), response.getId());
        Assertions.assertEquals(user.getName(), response.getName());
    }

    @Test
    void deleteUserNotFoundShouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUserNoExceptions() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1);
    }
}