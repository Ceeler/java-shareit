package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.util.List;

public interface UserMapper {

    UserResponse toDto(User user);

    User toModel(UserCreateRequest userDto);

    List<UserResponse> toDtoList(List<User> users);

    void updateModelFromDto(User model, UserUpdateRequest userDto);

}
