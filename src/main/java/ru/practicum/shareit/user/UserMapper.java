package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.BaseMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class UserMapper implements BaseMapper<User, UserDto> {

    private static final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public UserDto toDto(User model) {
        return UserDto.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }

    @Override
    public User toModel(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    @Override
    public List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateModelFromDto(User model, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            model.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank() && isValidEmail(userDto.getEmail())) {
            model.setEmail(userDto.getEmail());
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
    }
}
