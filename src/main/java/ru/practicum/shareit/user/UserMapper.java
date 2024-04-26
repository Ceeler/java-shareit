package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;


public class UserMapper {

    public static UserResponse toDto(User model) {
        return UserResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }


    public static User toModel(UserCreateRequest userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }


    public static List<UserResponse> toDtoList(List<User> users) {
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public static void updateModelFromDto(User model, UserUpdateRequest userDto) {
        if (userDto.getName() != null) {
            model.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            model.setEmail(userDto.getEmail());
        }
    }

}
