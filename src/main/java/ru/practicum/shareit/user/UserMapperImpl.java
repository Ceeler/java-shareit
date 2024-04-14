package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.request.UserCreateRequest;
import ru.practicum.shareit.user.dto.request.UserUpdateRequest;
import ru.practicum.shareit.user.dto.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toDto(User model) {
        return UserResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }

    @Override
    public User toModel(UserCreateRequest userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    @Override
    public List<UserResponse> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateModelFromDto(User model, UserUpdateRequest userDto) {
        if (userDto.getName() != null) {
            model.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            model.setEmail(userDto.getEmail());
        }
    }

}
